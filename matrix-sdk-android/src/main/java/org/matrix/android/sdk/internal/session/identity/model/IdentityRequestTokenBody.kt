

package org.matrix.android.sdk.internal.session.identity.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

private interface IdentityRequestTokenBody {
    
    val clientSecret: String

    val sendAttempt: Int
}

@JsonClass(generateAdapter = true)
internal data class IdentityRequestTokenForEmailBody(
        @Json(name = "client_secret")
        override val clientSecret: String,

        
        @Json(name = "send_attempt")
        override val sendAttempt: Int,

        
        @Json(name = "email")
        val email: String
) : IdentityRequestTokenBody

@JsonClass(generateAdapter = true)
internal data class IdentityRequestTokenForMsisdnBody(
        @Json(name = "client_secret")
        override val clientSecret: String,

        
        @Json(name = "send_attempt")
        override val sendAttempt: Int,

        
        @Json(name = "phone_number")
        val phoneNumber: String,

        
        @Json(name = "country")
        val countryCode: String
) : IdentityRequestTokenBody
