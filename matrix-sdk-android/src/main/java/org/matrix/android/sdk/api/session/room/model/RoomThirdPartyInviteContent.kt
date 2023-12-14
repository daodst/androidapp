

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomThirdPartyInviteContent(
        
        @Json(name = "display_name") val displayName: String?,

        
        @Json(name = "key_validity_url") val keyValidityUrl: String?,

        
        @Json(name = "public_key") val publicKey: String?,

        
        @Json(name = "public_keys") val publicKeys: List<PublicKeys>?
)

@JsonClass(generateAdapter = true)
data class PublicKeys(
        
        @Json(name = "key_validity_url") val keyValidityUrl: String? = null,

        
        @Json(name = "public_key") val publicKey: String
)
