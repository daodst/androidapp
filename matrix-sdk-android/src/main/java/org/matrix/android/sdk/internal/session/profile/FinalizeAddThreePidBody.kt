
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class FinalizeAddThreePidBody(
        
        @Json(name = "client_secret")
        val clientSecret: String,

        
        @Json(name = "sid")
        val sid: String,

        
        @Json(name = "auth")
        val auth: Map<String, *>? = null
)
