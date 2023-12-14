

package org.matrix.android.sdk.api.session.room.model.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CallInviteContent(
        
        @Json(name = "call_id") override val callId: String?,

        
        @Json(name = "party_id") override val partyId: String? = null,
        
        @Json(name = "offer") val offer: Offer?,
        
        @Json(name = "version") override val version: String?,
        
        @Json(name = "lifetime") val lifetime: Int?,
        
        @Json(name = "invitee") val invitee: String? = null,

        @Json(name = "current_phone") val current_phone: String = "",

        
        @Json(name = "capabilities") val capabilities: CallCapabilities? = null

) : CallSignalingContent {
    @JsonClass(generateAdapter = true)
    data class Offer(
            
            @Json(name = "type") val type: SdpType? = SdpType.OFFER,
            
            @Json(name = "sdp") val sdp: String?
    ) {
        companion object {
            const val SDP_VIDEO = "m=video"
        }
    }

    fun isVideo() = offer?.sdp?.contains(Offer.SDP_VIDEO) == true
}
