

package org.matrix.android.sdk.api.session.room.model.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CallNegotiateContent(
        
        @Json(name = "call_id") override val callId: String,
        
        @Json(name = "party_id") override val partyId: String? = null,
        
        @Json(name = "lifetime") val lifetime: Int?,
        
        @Json(name = "description") val description: Description? = null,

        
        @Json(name = "version") override val version: String?

) : CallSignalingContent {
    @JsonClass(generateAdapter = true)
    data class Description(
            
            @Json(name = "type") val type: SdpType?,
            
            @Json(name = "sdp") val sdp: String?
    )
}
