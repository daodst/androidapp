

package org.matrix.android.sdk.internal.session.room.create

import com.zhuinden.monarchy.Monarchy
import io.realm.RealmConfiguration
import kotlinx.coroutines.TimeoutCancellationException
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.room.alias.RoomAliasError
import org.matrix.android.sdk.api.session.room.failure.CreateRoomFailure
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset
import org.matrix.android.sdk.internal.database.awaitNotEmptyResult
import org.matrix.android.sdk.internal.database.awaitTransaction
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntityFields
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.room.RoomAPI
import org.matrix.android.sdk.internal.session.room.alias.RoomAliasAvailabilityChecker
import org.matrix.android.sdk.internal.session.room.read.SetReadMarkersTask
import org.matrix.android.sdk.internal.session.user.accountdata.DirectChatsHelper
import org.matrix.android.sdk.internal.session.user.accountdata.UpdateUserAccountDataTask
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.awaitTransaction
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal interface CreateRoomTask : Task<CreateRoomParams, String>

internal class DefaultCreateRoomTask @Inject constructor(
        private val roomAPI: RoomAPI,
        @SessionDatabase private val monarchy: Monarchy,
        private val aliasAvailabilityChecker: RoomAliasAvailabilityChecker,
        private val directChatsHelper: DirectChatsHelper,
        private val updateUserAccountDataTask: UpdateUserAccountDataTask,
        private val readMarkersTask: SetReadMarkersTask,
        @SessionDatabase
        private val realmConfiguration: RealmConfiguration,
        private val createRoomBodyBuilder: CreateRoomBodyBuilder,
        private val globalErrorReceiver: GlobalErrorReceiver
) : CreateRoomTask {

    override suspend fun execute(params: CreateRoomParams): String {
        val otherUserId = if (params.isDirect()) {
            params.getFirstInvitedUserId()
                    ?: throw IllegalStateException("You can't create a direct room without an invitedUser")
        } else null

        if (params.preset == CreateRoomPreset.PRESET_PUBLIC_CHAT) {
            try {
                aliasAvailabilityChecker.check(params.roomAliasName)
            } catch (aliasError: RoomAliasError) {
                throw CreateRoomFailure.AliasError(aliasError)
            }
        }

        val createRoomBody = createRoomBodyBuilder.build(params)

        val createRoomResponse = try {
            executeRequest(globalErrorReceiver) {
                roomAPI.createRoom(createRoomBody)
            }
        } catch (throwable: Throwable) {
            if (throwable is Failure.ServerError) {
                if (throwable.httpCode == 403 &&
                        throwable.error.code == MatrixError.M_FORBIDDEN &&
                        throwable.error.message.startsWith("Federation denied with")) {
                    throw CreateRoomFailure.CreatedWithFederationFailure(throwable.error)
                } else if (throwable.httpCode == 400 &&
                        throwable.error.code == MatrixError.M_UNKNOWN &&
                        throwable.error.message == "Invalid characters in room alias") {
                    throw CreateRoomFailure.AliasError(RoomAliasError.AliasInvalid)
                }
            }
            throw throwable
        }
        val roomId = createRoomResponse.roomId
        
        try {
            awaitNotEmptyResult(realmConfiguration, TimeUnit.MINUTES.toMillis(1L)) { realm ->
                realm.where(RoomSummaryEntity::class.java)
                        .equalTo(RoomSummaryEntityFields.ROOM_ID, roomId)
                        .equalTo(RoomSummaryEntityFields.MEMBERSHIP_STR, Membership.JOIN.name)
            }
        } catch (exception: TimeoutCancellationException) {
            throw CreateRoomFailure.CreatedWithTimeout(roomId)
        }

        awaitTransaction(realmConfiguration) {
            RoomSummaryEntity.where(it, roomId).findFirst()?.lastActivityTime = System.currentTimeMillis()
        }

        if (otherUserId != null) {
            handleDirectChatCreation(roomId, otherUserId)
        }
        setReadMarkers(roomId)
        return roomId
    }

    private suspend fun handleDirectChatCreation(roomId: String, otherUserId: String) {
        monarchy.awaitTransaction { realm ->
            RoomSummaryEntity.where(realm, roomId).findFirst()?.apply {
                this.directUserId = otherUserId
                this.isDirect = true
            }
        }
        val directChats = directChatsHelper.getLocalDirectMessages()
        updateUserAccountDataTask.execute(UpdateUserAccountDataTask.DirectChatParams(directMessages = directChats))
    }

    private suspend fun setReadMarkers(roomId: String) {
        val setReadMarkerParams = SetReadMarkersTask.Params(roomId, forceReadReceipt = true, forceReadMarker = true)
        return readMarkersTask.execute(setReadMarkerParams)
    }

    
    private fun CreateRoomParams.isDirect(): Boolean {
        return preset == CreateRoomPreset.PRESET_TRUSTED_PRIVATE_CHAT &&
                isDirect == true
    }

    
    private fun CreateRoomParams.getFirstInvitedUserId(): String? {
        return invitedUserIds.firstOrNull() ?: invite3pids.firstOrNull()?.value
    }
}
