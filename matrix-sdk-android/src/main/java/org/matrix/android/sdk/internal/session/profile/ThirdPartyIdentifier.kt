
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ThirdPartyIdentifier(
        
        @Json(name = "medium")
        val medium: String? = null,

        
        @Json(name = "address")
        val address: String? = null,

        
        @Json(name = "validated_at")
        val validatedAt: Any? = null,

        
        @Json(name = "added_at")
        val addedAt: Any? = null
) {
    companion object {
        const val MEDIUM_EMAIL = "email"
        const val MEDIUM_MSISDN = "msisdn"

        val SUPPORTED_MEDIUM = listOf(MEDIUM_EMAIL, MEDIUM_MSISDN)
    }
}
