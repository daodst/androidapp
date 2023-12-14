
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AddMsisdnBody(
        
        @Json(name = "client_secret")
        val clientSecret: String,

        
        @Json(name = "country")
        val country: String,

        
        @Json(name = "phone_number")
        val phoneNumber: String,

        
        @Json(name = "send_attempt")
        val sendAttempt: Int
)
