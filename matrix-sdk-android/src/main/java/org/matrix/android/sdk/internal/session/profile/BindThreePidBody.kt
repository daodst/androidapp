
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BindThreePidBody(
        
        @Json(name = "client_secret")
        val clientSecret: String,

        
        @Json(name = "id_server")
        val identityServerUrlWithoutProtocol: String,

        
        @Json(name = "id_access_token")
        val identityServerAccessToken: String,

        
        @Json(name = "sid")
        val sid: String
)
