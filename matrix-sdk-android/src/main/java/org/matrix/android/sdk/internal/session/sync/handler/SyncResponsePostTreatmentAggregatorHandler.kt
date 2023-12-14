

package org.matrix.android.sdk.internal.session.sync.handler

import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.internal.database.model.UserEntity
import org.matrix.android.sdk.internal.database.query.contains
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.profile.GetProfileInfoTask
import org.matrix.android.sdk.internal.session.sync.RoomSyncEphemeralTemporaryStore
import org.matrix.android.sdk.internal.session.sync.SyncResponsePostTreatmentAggregator
import org.matrix.android.sdk.internal.session.sync.model.accountdata.toMutable
import org.matrix.android.sdk.internal.session.user.UserEntityFactory
import org.matrix.android.sdk.internal.session.user.accountdata.DirectChatsHelper
import org.matrix.android.sdk.internal.session.user.accountdata.UpdateUserAccountDataTask
import org.matrix.android.sdk.internal.session.user.getAddressByUid
import org.matrix.android.sdk.internal.util.awaitTransaction
import timber.log.Timber
import javax.inject.Inject

internal class SyncResponsePostTreatmentAggregatorHandler @Inject constructor(
        private val directChatsHelper: DirectChatsHelper,
        private val ephemeralTemporaryStore: RoomSyncEphemeralTemporaryStore,
        private val updateUserAccountDataTask: UpdateUserAccountDataTask,
        private val getProfileInfoTask: GetProfileInfoTask,
        @SessionDatabase private val monarchy: Monarchy,
) {
    suspend fun handle(aggregator: SyncResponsePostTreatmentAggregator) {
        cleanupEphemeralFiles(aggregator.ephemeralFilesToDelete)
        updateDirectUserIds(aggregator.directChatsToCheck)
        fetchAndUpdateUsers(aggregator.userIdsToFetch)
    }

    private fun cleanupEphemeralFiles(ephemeralFilesToDelete: List<String>) {
        ephemeralFilesToDelete.forEach {
            ephemeralTemporaryStore.delete(it)
        }
    }

    private suspend fun updateDirectUserIds(directUserIdsToUpdate: Map<String, String>) {
        val directChats = directChatsHelper.getLocalDirectMessages().toMutable()
        var hasUpdate = false
        directUserIdsToUpdate.forEach { (roomId, candidateUserId) ->
            
            val currentDirectUserId = directChats.firstNotNullOfOrNull { (userId, roomIds) -> userId.takeIf { roomId in roomIds } }
            
            if (currentDirectUserId != null && !MatrixPatterns.isUserId(currentDirectUserId)) {
                
                directChats
                        .getOrPut(candidateUserId) { arrayListOf() }
                        .apply {
                            if (!contains(roomId)) {
                                hasUpdate = true
                                add(roomId)
                            }
                        }

                
                hasUpdate = hasUpdate or (directChats[currentDirectUserId]?.remove(roomId) == true)
                
                hasUpdate = hasUpdate or (directChats.takeIf { it[currentDirectUserId].isNullOrEmpty() }?.remove(currentDirectUserId) != null)
            }
        }
        if (hasUpdate) {
            updateUserAccountDataTask.execute(UpdateUserAccountDataTask.DirectChatParams(directMessages = directChats))
        }
    }

    private suspend fun fetchAndUpdateUsers(userIdsToFetch: List<String>) {
        fetchUsers(userIdsToFetch)
                .takeIf { it.isNotEmpty() }
                ?.saveLocally()
    }

    private suspend fun fetchUsers(userIdsToFetch: List<String>) = userIdsToFetch.mapNotNull {
        tryOrNull {
            val profileJson = getProfileInfoTask.execute(GetProfileInfoTask.Params(it))
            User.fromJson(it, profileJson)
        }
    }

    private suspend fun List<User>.saveLocally() {
        val userEntities = map { user -> UserEntityFactory.create(user) }
        monarchy.awaitTransaction { realm ->

            userEntities.forEach {
                val address = getAddressByUid(it.userId)
                val findFirst = UserEntity.contains(realm, address).findFirst()
                Timber.i("-----createOrUpdate------$findFirst--------saveLocally---------")
                if (null != findFirst && !it.userId.equals(findFirst.userId)) {
                    
                    findFirst.deleteFromRealm()
                }
            }
            realm.insertOrUpdate(userEntities)
        }
    }
}
