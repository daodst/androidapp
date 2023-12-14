

package org.matrix.android.sdk.api.session.crypto.model

import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.crypto.OutgoingGossipingRequest


@JsonClass(generateAdapter = true)
data class OutgoingRoomKeyRequest(
        
        val requestBody: RoomKeyRequestBody?,
        
        override val recipients: Map<String, List<String>>,
        
        
        
        override val requestId: String, 
        override val state: OutgoingGossipingRequestState
        
        
) : OutgoingGossipingRequest {

    
    val roomId: String?
        get() = requestBody?.roomId

    
    val sessionId: String?
        get() = requestBody?.sessionId
}
