
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UnbindThreePidBody(
        
        @Json(name = "id_server")
        val identityServerUrlWithoutProtocol: String?,

        
        @Json(name = "medium")
        val medium: String,

        
        @Json(name = "address")
        val address: String
)
