

package org.matrix.android.sdk.api.session.crypto.model

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.internal.crypto.IncomingShareRequestCommon


data class IncomingRoomKeyRequest(
        
        override val userId: String? = null,

        
        override val deviceId: String? = null,

        
        override val requestId: String? = null,

        
        val requestBody: RoomKeyRequestBody? = null,

        val state: GossipingRequestState = GossipingRequestState.NONE,

        
        @Transient
        var share: Runnable? = null,

        
        @Transient
        var ignore: Runnable? = null,
        override val localCreationTimestamp: Long?
) : IncomingShareRequestCommon {
    companion object {
        
        fun fromEvent(event: Event): IncomingRoomKeyRequest? {
            return event.getClearContent()
                    .toModel<RoomKeyShareRequest>()
                    ?.let {
                        IncomingRoomKeyRequest(
                                userId = event.senderId,
                                deviceId = it.requestingDeviceId,
                                requestId = it.requestId,
                                requestBody = it.body ?: RoomKeyRequestBody(),
                                localCreationTimestamp = event.ageLocalTs ?: System.currentTimeMillis()
                        )
                    }
        }
    }
}
