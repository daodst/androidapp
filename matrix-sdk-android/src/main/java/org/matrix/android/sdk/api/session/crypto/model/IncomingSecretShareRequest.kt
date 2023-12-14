

package org.matrix.android.sdk.api.session.crypto.model

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.internal.crypto.IncomingShareRequestCommon


data class IncomingSecretShareRequest(
        
        override val userId: String? = null,

        
        override val deviceId: String? = null,

        
        override val requestId: String? = null,

        
        val secretName: String? = null,

        
        @Transient
        var share: ((String) -> Unit)? = null,

        
        @Transient
        var ignore: Runnable? = null,

        override val localCreationTimestamp: Long?

) : IncomingShareRequestCommon {
    companion object {
        
        fun fromEvent(event: Event): IncomingSecretShareRequest? {
            return event.getClearContent()
                    .toModel<SecretShareRequest>()
                    ?.let {
                        IncomingSecretShareRequest(
                                userId = event.senderId,
                                deviceId = it.requestingDeviceId,
                                requestId = it.requestId,
                                secretName = it.secretName,
                                localCreationTimestamp = event.ageLocalTs ?: System.currentTimeMillis()
                        )
                    }
        }
    }
}
