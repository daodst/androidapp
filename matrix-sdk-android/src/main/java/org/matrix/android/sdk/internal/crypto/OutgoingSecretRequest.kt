

package org.matrix.android.sdk.internal.crypto

import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.OutgoingGossipingRequestState


@JsonClass(generateAdapter = true)
internal class OutgoingSecretRequest(
        
        val secretName: String?,
        
        override var recipients: Map<String, List<String>>,
        
        
        
        override var requestId: String,
        
        override var state: OutgoingGossipingRequestState) : OutgoingGossipingRequest {

    
}
