

package org.matrix.android.sdk.api.session.room.model.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CallAnswerContent(
        
        @Json(name = "call_id") override val callId: String,
        
        @Json(name = "party_id") override val partyId: String? = null,
        
        @Json(name = "answer") val answer: Answer,
        
        @Json(name = "version") override val version: String?,
        
        @Json(name = "capabilities") val capabilities: CallCapabilities? = null
) : CallSignalingContent {

    @JsonClass(generateAdapter = true)
    data class Answer(
            
            @Json(name = "type") val type: SdpType = SdpType.ANSWER,
            
            @Json(name = "sdp") val sdp: String
    )
}
