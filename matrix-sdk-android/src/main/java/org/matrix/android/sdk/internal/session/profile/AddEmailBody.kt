
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AddEmailBody(
        
        @Json(name = "client_secret")
        val clientSecret: String,

        
        @Json(name = "email")
        val email: String,

        
        @Json(name = "send_attempt")
        val sendAttempt: Int
)
