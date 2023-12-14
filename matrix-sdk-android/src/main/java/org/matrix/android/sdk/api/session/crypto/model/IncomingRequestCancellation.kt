

package org.matrix.android.sdk.api.session.crypto.model

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.internal.crypto.IncomingShareRequestCommon
import org.matrix.android.sdk.internal.crypto.model.rest.ShareRequestCancellation


data class IncomingRequestCancellation(
        
        override val userId: String? = null,

        
        override val deviceId: String? = null,

        
        override val requestId: String? = null,
        override val localCreationTimestamp: Long?
) : IncomingShareRequestCommon {
    companion object {
        
        fun fromEvent(event: Event): IncomingRequestCancellation? {
            return event.getClearContent()
                    .toModel<ShareRequestCancellation>()
                    ?.let {
                        IncomingRequestCancellation(
                                userId = event.senderId,
                                deviceId = it.requestingDeviceId,
                                requestId = it.requestId,
                                localCreationTimestamp = event.ageLocalTs ?: System.currentTimeMillis()
                        )
                    }
        }
    }
}
