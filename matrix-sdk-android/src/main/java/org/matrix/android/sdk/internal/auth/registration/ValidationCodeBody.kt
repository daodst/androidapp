

package org.matrix.android.sdk.internal.auth.registration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class ValidationCodeBody(
        @Json(name = "client_secret")
        val clientSecret: String,

        @Json(name = "sid")
        val sid: String,

        @Json(name = "token")
        val code: String
)
