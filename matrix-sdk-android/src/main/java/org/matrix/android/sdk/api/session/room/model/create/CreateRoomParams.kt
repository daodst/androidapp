

package org.matrix.android.sdk.api.session.room.model.create

import android.net.Uri
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.room.model.GuestAccess
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibility

open class CreateRoomParams {
    
    var visibility: RoomDirectoryVisibility? = null

    
    var roomAliasName: String? = null

    
    var name: String? = null

    
    var topic: String? = null

    
    var avatarUri: Uri? = null

    
    val invitedUserIds = mutableListOf<String>()

    
    val invite3pids = mutableListOf<ThreePid>()

    
    var guestAccess: GuestAccess? = null

    
    var enableEncryptionIfInvitedUsersSupportIt: Boolean = false

    
    var preset: CreateRoomPreset? = null

    
    var isDirect: Boolean? = null

    
    val creationContent = mutableMapOf<String, Any>()

    
    val initialStates = mutableListOf<CreateRoomStateEvent>()

    
    var disableFederation = false
        set(value) {
            field = value
            if (value) {
                creationContent[CREATION_CONTENT_KEY_M_FEDERATE] = false
            } else {
                
                creationContent.remove(CREATION_CONTENT_KEY_M_FEDERATE)
            }
        }

    var roomType: String? = null 
        set(value) {
            field = value
            if (value != null) {
                creationContent[CREATION_CONTENT_KEY_ROOM_TYPE] = value
            } else {
                
                creationContent.remove(CREATION_CONTENT_KEY_ROOM_TYPE)
            }
        }

    
    var powerLevelContentOverride: PowerLevelsContent? = null

    
    fun setDirectMessage() {
        preset = CreateRoomPreset.PRESET_TRUSTED_PRIVATE_CHAT
        isDirect = true
    }

    
    var algorithm: String? = null
        private set

    var historyVisibility: RoomHistoryVisibility? = null

    fun enableEncryption() {
        algorithm = MXCRYPTO_ALGORITHM_MEGOLM
    }

    var roomVersion: String? = null

    var featurePreset: RoomFeaturePreset? = null

    companion object {
        private const val CREATION_CONTENT_KEY_M_FEDERATE = "m.federate"
        private const val CREATION_CONTENT_KEY_ROOM_TYPE = "type"
    }
}
