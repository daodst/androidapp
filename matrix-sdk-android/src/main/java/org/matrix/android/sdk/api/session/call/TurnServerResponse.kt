

package org.matrix.android.sdk.api.session.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class TurnServerResponse(
        
        @Json(name = "username") val username: String?,

        
        @Json(name = "password") val password: String?,

        
        @Json(name = "uris") val uris: List<String>?,

        
        @Json(name = "ttl") val ttl: Int?
)
