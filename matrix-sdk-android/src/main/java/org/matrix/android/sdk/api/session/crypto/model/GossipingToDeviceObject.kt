

package org.matrix.android.sdk.api.session.crypto.model


interface GossipingToDeviceObject : SendToDeviceObject {

    val action: String?

    val requestingDeviceId: String?

    val requestId: String?

    companion object {
        const val ACTION_SHARE_REQUEST = "request"
        const val ACTION_SHARE_CANCELLATION = "request_cancellation"
    }
}
