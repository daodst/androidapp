

package org.matrix.android.sdk.internal.session.room.membership

import io.realm.Realm
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.internal.database.model.RoomMemberSummaryEntity
import org.matrix.android.sdk.internal.database.model.presence.UserPresenceEntity
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.session.events.getFixedRoomMemberContent
import org.matrix.android.sdk.internal.session.sync.SyncResponsePostTreatmentAggregator
import org.matrix.android.sdk.internal.session.user.UserEntityFactory
import javax.inject.Inject

internal class RoomMemberEventHandler @Inject constructor(
        @UserId private val myUserId: String
) {

    fun handle(realm: Realm,
               roomId: String,
               event: Event,
               isInitialSync: Boolean,
               aggregator: SyncResponsePostTreatmentAggregator? = null): Boolean {
        if (event.type != EventType.STATE_ROOM_MEMBER) {
            return false
        }
        val eventUserId = event.stateKey ?: return false
        val roomMember = event.getFixedRoomMemberContent() ?: return false

        return if (isInitialSync) {
            handleInitialSync(realm, roomId, myUserId, eventUserId, roomMember, aggregator)
        } else {
            handleIncrementalSync(
                    realm,
                    roomId,
                    eventUserId,
                    roomMember,
                    event.resolvedPrevContent(),
                    aggregator
            )
        }
    }

    private fun handleInitialSync(realm: Realm,
                                  roomId: String,
                                  currentUserId: String,
                                  eventUserId: String,
                                  roomMember: RoomMemberContent,
                                  aggregator: SyncResponsePostTreatmentAggregator?): Boolean {
        if (currentUserId != eventUserId) {
            saveUserEntityLocallyIfNecessary(realm, eventUserId, roomMember)
        }
        saveRoomMemberEntityLocally(realm, roomId, eventUserId, roomMember)
        updateDirectChatsIfNecessary(roomId, roomMember, aggregator)
        return true
    }

    private fun saveRoomMemberEntityLocally(realm: Realm,
                                            roomId: String,
                                            userId: String,
                                            roomMember: RoomMemberContent) {
        val roomMemberEntity = RoomMemberEntityFactory.create(
                roomId,
                userId,
                roomMember,
                
                
                getExistingPresenceState(realm, roomId, userId))
        realm.insertOrUpdate(roomMemberEntity)
    }

    
    private fun getExistingPresenceState(realm: Realm, roomId: String, userId: String): UserPresenceEntity? {
        return RoomMemberSummaryEntity.where(realm, roomId, userId).findFirst()?.userPresenceEntity
    }

    private fun saveUserEntityLocallyIfNecessary(realm: Realm,
                                                 userId: String,
                                                 roomMember: RoomMemberContent) {
        if (roomMember.membership.isActive()) {
            saveUserLocally(realm, userId, roomMember)
        }
    }

    private fun saveUserLocally(realm: Realm, userId: String, roomMember: RoomMemberContent) {
        val userEntity = UserEntityFactory.create(userId, roomMember)
        realm.insertOrUpdate(userEntity)
    }

    private fun updateDirectChatsIfNecessary(roomId: String,
                                             roomMember: RoomMemberContent,
                                             aggregator: SyncResponsePostTreatmentAggregator?) {
        
        
        val mxId = roomMember.thirdPartyInvite?.signed?.mxid
        if (mxId != null && mxId != myUserId) {
            aggregator?.directChatsToCheck?.put(roomId, mxId)
        }
    }

    private fun handleIncrementalSync(realm: Realm,
                                      roomId: String,
                                      eventUserId: String,
                                      roomMember: RoomMemberContent,
                                      prevContent: Content?,
                                      aggregator: SyncResponsePostTreatmentAggregator?): Boolean {
        if (aggregator != null) {
            val previousDisplayName = prevContent?.get("displayname") as? String
            val previousAvatar = prevContent?.get("avatar_url") as? String

            if (previousDisplayName != roomMember.displayName || previousAvatar != roomMember.avatarUrl) {
                aggregator.userIdsToFetch.add(eventUserId)
            }
        }

        saveRoomMemberEntityLocally(realm, roomId, eventUserId, roomMember)
        
        updateDirectChatsIfNecessary(roomId, roomMember, aggregator)
        return true
    }
}
