
package org.matrix.android.sdk.internal.session.identity.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class IdentityRequestOwnershipParams(
        
        @Json(name = "client_secret")
        val clientSecret: String,

        
        @Json(name = "sid")
        val sid: String,

        
        @Json(name = "token")
        val token: String
)
