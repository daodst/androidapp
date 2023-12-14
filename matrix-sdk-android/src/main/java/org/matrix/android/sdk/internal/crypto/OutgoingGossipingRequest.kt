

package org.matrix.android.sdk.internal.crypto

import org.matrix.android.sdk.api.session.crypto.model.OutgoingGossipingRequestState

internal interface OutgoingGossipingRequest {
    val recipients: Map<String, List<String>>
    val requestId: String
    val state: OutgoingGossipingRequestState
    
    
}
