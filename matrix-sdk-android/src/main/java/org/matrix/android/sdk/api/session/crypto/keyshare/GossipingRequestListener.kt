

package org.matrix.android.sdk.api.session.crypto.keyshare

import org.matrix.android.sdk.api.session.crypto.model.IncomingRequestCancellation
import org.matrix.android.sdk.api.session.crypto.model.IncomingRoomKeyRequest
import org.matrix.android.sdk.api.session.crypto.model.IncomingSecretShareRequest


interface GossipingRequestListener {
    
    fun onRoomKeyRequest(request: IncomingRoomKeyRequest)

    
    fun onSecretShareRequest(request: IncomingSecretShareRequest): Boolean

    
    fun onRoomKeyRequestCancellation(request: IncomingRequestCancellation)
}
