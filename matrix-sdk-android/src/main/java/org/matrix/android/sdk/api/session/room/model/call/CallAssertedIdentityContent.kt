

package org.matrix.android.sdk.api.session.room.model.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CallAssertedIdentityContent(
        
        @Json(name = "call_id") override val callId: String,
        
        @Json(name = "party_id") override val partyId: String? = null,
        
        @Json(name = "version") override val version: String?,

        
        @Json(name = "asserted_identity") val assertedIdentity: AssertedIdentity? = null
) : CallSignalingContent {

    
    @JsonClass(generateAdapter = true)
    data class AssertedIdentity(
            @Json(name = "id") val id: String? = null,
            @Json(name = "display_name") val displayName: String? = null,
            @Json(name = "avatar_url") val avatarUrl: String? = null
    )
}
