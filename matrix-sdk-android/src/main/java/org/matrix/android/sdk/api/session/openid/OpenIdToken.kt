

package org.matrix.android.sdk.api.session.openid

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenIdToken(
        
        @Json(name = "access_token")
        val accessToken: String,

        
        @Json(name = "token_type")
        val tokenType: String,

        
        @Json(name = "matrix_server_name")
        val matrixServerName: String,

        
        @Json(name = "expires_in")
        val expiresIn: Int
)
