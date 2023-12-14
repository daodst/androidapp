

package org.matrix.android.sdk.internal.session.sync.handler

import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.api.failure.InitialSyncRequestReason
import org.matrix.android.sdk.api.pushrules.RuleScope
import org.matrix.android.sdk.api.pushrules.RuleSetKey
import org.matrix.android.sdk.api.pushrules.rest.GetPushRulesResponse
import org.matrix.android.sdk.api.session.accountdata.UserAccountDataEvent
import org.matrix.android.sdk.api.session.accountdata.UserAccountDataTypes
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.sync.model.InvitedRoomSync
import org.matrix.android.sdk.api.session.sync.model.UserAccountDataSync
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.database.mapper.ContentMapper
import org.matrix.android.sdk.internal.database.mapper.PushRulesMapper
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.model.BreadcrumbsEntity
import org.matrix.android.sdk.internal.database.model.IgnoredUserEntity
import org.matrix.android.sdk.internal.database.model.PushRulesEntity
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntityFields
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.model.UserAccountDataEntity
import org.matrix.android.sdk.internal.database.model.UserAccountDataEntityFields
import org.matrix.android.sdk.internal.database.model.deleteOnCascade
import org.matrix.android.sdk.internal.database.query.findAllFrom
import org.matrix.android.sdk.internal.database.query.getDirectRooms
import org.matrix.android.sdk.internal.database.query.getOrCreate
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.di.SessionId
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.SessionListeners
import org.matrix.android.sdk.internal.session.dispatchTo
import org.matrix.android.sdk.internal.session.room.RoomAvatarResolver
import org.matrix.android.sdk.internal.session.room.membership.RoomDisplayNameResolver
import org.matrix.android.sdk.internal.session.room.membership.RoomMemberHelper
import org.matrix.android.sdk.internal.session.sync.model.accountdata.BreadcrumbsContent
import org.matrix.android.sdk.internal.session.sync.model.accountdata.DirectMessagesContent
import org.matrix.android.sdk.internal.session.sync.model.accountdata.IgnoredUsersContent
import org.matrix.android.sdk.internal.session.sync.model.accountdata.toMutable
import org.matrix.android.sdk.internal.session.user.accountdata.DirectChatsHelper
import org.matrix.android.sdk.internal.session.user.accountdata.UpdateUserAccountDataTask
import timber.log.Timber
import javax.inject.Inject

internal class UserAccountDataSyncHandler @Inject constructor(
        @SessionDatabase private val monarchy: Monarchy,
        @UserId private val userId: String,
        private val directChatsHelper: DirectChatsHelper,
        private val updateUserAccountDataTask: UpdateUserAccountDataTask,
        private val roomAvatarResolver: RoomAvatarResolver,
        private val roomDisplayNameResolver: RoomDisplayNameResolver,
        @SessionId private val sessionId: String,
        private val sessionManager: SessionManager,
        private val sessionListeners: SessionListeners
) {

    fun handle(realm: Realm, accountData: UserAccountDataSync?) {
        accountData?.list?.forEach { event ->
            
            handleGenericAccountData(realm, event.type, event.content)
            when (event.type) {
                UserAccountDataTypes.TYPE_DIRECT_MESSAGES   -> handleDirectChatRooms(realm, event)
                UserAccountDataTypes.TYPE_PUSH_RULES        -> handlePushRules(realm, event)
                UserAccountDataTypes.TYPE_IGNORED_USER_LIST -> handleIgnoredUsers(realm, event)
                UserAccountDataTypes.TYPE_BREADCRUMBS       -> handleBreadcrumbs(realm, event)
            }
        }
    }

    
    suspend fun synchronizeWithServerIfNeeded(invites: Map<String, InvitedRoomSync>) {
        if (invites.isNullOrEmpty()) return
        val directChats = directChatsHelper.getLocalDirectMessages().toMutable()
        var hasUpdate = false
        monarchy.doWithRealm { realm ->
            invites.forEach { (roomId, _) ->
                val myUserStateEvent = RoomMemberHelper(realm, roomId).getLastStateEvent(userId)
                val inviterId = myUserStateEvent?.sender
                val myUserRoomMember: RoomMemberContent? = myUserStateEvent?.let { it.asDomain().content?.toModel() }
                val isDirect = myUserRoomMember?.isDirect
                if (inviterId != null && inviterId != userId && isDirect == true) {
                    directChats
                            .getOrPut(inviterId, { arrayListOf() })
                            .apply {
                                if (contains(roomId)) {
                                    Timber.v("Direct chats already include room $roomId with user $inviterId")
                                } else {
                                    add(roomId)
                                    hasUpdate = true
                                }
                            }
                }
            }
        }
        if (hasUpdate) {
            val updateUserAccountParams = UpdateUserAccountDataTask.DirectChatParams(
                    directMessages = directChats
            )
            updateUserAccountDataTask.execute(updateUserAccountParams)
        }
    }

    private fun handlePushRules(realm: Realm, event: UserAccountDataEvent) {
        val pushRules = event.content.toModel<GetPushRulesResponse>() ?: return
        realm.where(PushRulesEntity::class.java)
                .findAll()
                .forEach { it.deleteOnCascade() }

        
        val globalRules = pushRules.global

        val content = PushRulesEntity(RuleScope.GLOBAL).apply { kind = RuleSetKey.CONTENT }
        globalRules.content?.forEach { rule ->
            content.pushRules.add(PushRulesMapper.map(rule))
        }
        realm.insertOrUpdate(content)

        val override = PushRulesEntity(RuleScope.GLOBAL).apply { kind = RuleSetKey.OVERRIDE }
        globalRules.override?.forEach { rule ->
            PushRulesMapper.map(rule).also {
                override.pushRules.add(it)
            }
        }
        realm.insertOrUpdate(override)

        val rooms = PushRulesEntity(RuleScope.GLOBAL).apply { kind = RuleSetKey.ROOM }
        globalRules.room?.forEach { rule ->
            rooms.pushRules.add(PushRulesMapper.map(rule))
        }
        realm.insertOrUpdate(rooms)

        val senders = PushRulesEntity(RuleScope.GLOBAL).apply { kind = RuleSetKey.SENDER }
        globalRules.sender?.forEach { rule ->
            senders.pushRules.add(PushRulesMapper.map(rule))
        }
        realm.insertOrUpdate(senders)

        val underrides = PushRulesEntity(RuleScope.GLOBAL).apply { kind = RuleSetKey.UNDERRIDE }
        globalRules.underride?.forEach { rule ->
            underrides.pushRules.add(PushRulesMapper.map(rule))
        }
        realm.insertOrUpdate(underrides)
    }

    private fun handleDirectChatRooms(realm: Realm, event: UserAccountDataEvent) {
        val content = event.content.toModel<DirectMessagesContent>() ?: return
        content.forEach { (userId, roomIds) ->
            roomIds.forEach { roomId ->
                val roomSummaryEntity = RoomSummaryEntity.where(realm, roomId).findFirst()
                if (roomSummaryEntity != null) {
                    roomSummaryEntity.isDirect = true
                    roomSummaryEntity.directUserId = userId
                    
                    roomSummaryEntity.avatarUrl = roomAvatarResolver.resolve(realm, roomId)
                    roomSummaryEntity.setDisplayName(roomDisplayNameResolver.resolve(realm, roomId))
                }
            }
        }

        
        RoomSummaryEntity.getDirectRooms(realm, excludeRoomIds = content.values.flatten().toSet())
                .forEach {
                    it.isDirect = false
                    it.directUserId = null
                    
                    it.avatarUrl = roomAvatarResolver.resolve(realm, it.roomId)
                    it.setDisplayName(roomDisplayNameResolver.resolve(realm, it.roomId))
                }
    }

    private fun handleIgnoredUsers(realm: Realm, event: UserAccountDataEvent) {
        val userIds = event.content.toModel<IgnoredUsersContent>()?.ignoredUsers?.keys ?: return
        val currentIgnoredUsers = realm.where(IgnoredUserEntity::class.java).findAll()
        val currentIgnoredUserIds = currentIgnoredUsers.map { it.userId }
        
        currentIgnoredUsers.deleteAllFromRealm()
        
        userIds.forEach { realm.createObject(IgnoredUserEntity::class.java).apply { userId = it } }

        
        
        
        
        TimelineEventEntity.findAllFrom(realm, userIds)
                .also { Timber.d("Deleting ${it.size} TimelineEventEntity from ignored users") }
                .forEach {
                    it.deleteOnCascade(true)
                }

        
        val mustRefreshCache = currentIgnoredUserIds.any { currentIgnoredUserId -> currentIgnoredUserId !in userIds }
        if (mustRefreshCache) {
            Timber.d("A user has been unignored from another session, an initial sync should be performed")
            dispatchMustRefresh()
        }
    }

    private fun dispatchMustRefresh() {
        val session = sessionManager.getSessionComponent(sessionId)?.session()
        session.dispatchTo(sessionListeners) { safeSession, listener ->
            listener.onGlobalError(safeSession, GlobalError.InitialSyncRequest(InitialSyncRequestReason.IGNORED_USERS_LIST_CHANGE))
        }
    }

    private fun handleBreadcrumbs(realm: Realm, event: UserAccountDataEvent) {
        val recentRoomIds = event.content.toModel<BreadcrumbsContent>()?.recentRoomIds ?: return
        val entity = BreadcrumbsEntity.getOrCreate(realm)

        
        entity.recentRoomIds = RealmList<String>().apply { addAll(recentRoomIds) }

        
        
        RoomSummaryEntity.where(realm)
                .greaterThan(RoomSummaryEntityFields.BREADCRUMBS_INDEX, RoomSummary.NOT_IN_BREADCRUMBS)
                .findAll()
                .forEach {
                    it.breadcrumbsIndex = RoomSummary.NOT_IN_BREADCRUMBS
                }

        
        recentRoomIds.forEachIndexed { index, roomId ->
            RoomSummaryEntity.where(realm, roomId)
                    .findFirst()
                    ?.breadcrumbsIndex = index
        }
    }

    fun handleGenericAccountData(realm: Realm, type: String, content: Content?) {
        val existing = realm.where<UserAccountDataEntity>()
                .equalTo(UserAccountDataEntityFields.TYPE, type)
                .findFirst()
        if (existing != null) {
            
            existing.contentStr = ContentMapper.map(content)
        } else {
            realm.createObject(UserAccountDataEntity::class.java).let { accountDataEntity ->
                accountDataEntity.type = type
                accountDataEntity.contentStr = ContentMapper.map(content)
            }
        }
    }
}
