

package im.vector.app.features.call.lookup

import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.accountdata.RoomAccountDataTypes
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams

class CallUserMapper(private val session: Session, private val protocolsChecker: CallProtocolsChecker) {

    fun nativeRoomForVirtualRoom(roomId: String): String? {
        if (!protocolsChecker.supportVirtualRooms) return null
        val virtualRoom = session.getRoom(roomId) ?: return null
        val virtualRoomEvent = virtualRoom.getAccountDataEvent(RoomAccountDataTypes.EVENT_TYPE_VIRTUAL_ROOM)
        return virtualRoomEvent?.content?.toModel<RoomVirtualContent>()?.nativeRoomId
    }

    fun virtualRoomForNativeRoom(roomId: String): String? {
        if (!protocolsChecker.supportVirtualRooms) return null
        val virtualRoomEvents = session.accountDataService().getRoomAccountDataEvents(setOf(RoomAccountDataTypes.EVENT_TYPE_VIRTUAL_ROOM))
        return virtualRoomEvents.firstOrNull {
            val virtualRoomContent = it.content.toModel<RoomVirtualContent>()
            virtualRoomContent?.nativeRoomId == roomId
        }?.roomId
    }

    suspend fun getOrCreateVirtualRoomForRoom(roomId: String, opponentUserId: String): String? {
        protocolsChecker.awaitCheckProtocols()
        if (!protocolsChecker.supportVirtualRooms) return null
        val virtualUser = userToVirtualUser(opponentUserId) ?: return null
        val virtualRoomId = tryOrNull {
            ensureVirtualRoomExists(virtualUser, roomId)
        } ?: return null
        session.getRoom(virtualRoomId)?.markVirtual(roomId)
        return virtualRoomId
    }

    suspend fun onNewInvitedRoom(invitedRoomId: String) {
        protocolsChecker.awaitCheckProtocols()
        if (!protocolsChecker.supportVirtualRooms) return
        val invitedRoom = session.getRoom(invitedRoomId) ?: return
        val inviterId = invitedRoom.roomSummary()?.inviterId ?: return
        val nativeLookup = session.sipNativeLookup(inviterId).firstOrNull() ?: return
        if (nativeLookup.fields.containsKey("is_virtual")) {
            val nativeUser = nativeLookup.userId
            val nativeRoomId = session.getExistingDirectRoomWithUser(nativeUser)
            if (nativeRoomId != null) {
                
                
                
                invitedRoom.markVirtual(nativeRoomId)
            }
        }
    }

    private suspend fun userToVirtualUser(userId: String): String? {
        val results = session.sipVirtualLookup(userId)
        return results.firstOrNull()?.userId
    }

    private suspend fun Room.markVirtual(nativeRoomId: String) {
        val virtualRoomContent = RoomVirtualContent(nativeRoomId = nativeRoomId)
        updateAccountData(RoomAccountDataTypes.EVENT_TYPE_VIRTUAL_ROOM, virtualRoomContent.toContent())
    }

    private suspend fun ensureVirtualRoomExists(userId: String, nativeRoomId: String): String {
        val existingDMRoom = tryOrNull { session.getExistingDirectRoomWithUser(userId) }
        val roomId: String
        if (existingDMRoom != null) {
            roomId = existingDMRoom
        } else {
            val roomParams = CreateRoomParams().apply {
                invitedUserIds.add(userId)
                setDirectMessage()
                creationContent[RoomAccountDataTypes.EVENT_TYPE_VIRTUAL_ROOM] = nativeRoomId
            }
            roomId = session.createRoom(roomParams)
        }
        return roomId
    }
}
