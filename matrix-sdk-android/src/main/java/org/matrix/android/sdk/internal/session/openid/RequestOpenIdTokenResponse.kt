

package org.matrix.android.sdk.internal.session.openid

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RequestOpenIdTokenResponse(
        
        @Json(name = "access_token")
        val openIdToken: String,

        
        @Json(name = "token_type")
        val tokenType: String,

        
        @Json(name = "matrix_server_name")
        val matrixServerName: String,

        
        @Json(name = "expires_in")
        val expiresIn: Int
)
