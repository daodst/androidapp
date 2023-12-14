

package org.matrix.android.sdk.internal.federation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class FederationGetVersionResult(
        @Json(name = "server")
        val server: FederationGetVersionServer?
)

@JsonClass(generateAdapter = true)
internal data class FederationGetVersionServer(
        
        @Json(name = "name")
        val name: String?,
        
        @Json(name = "version")
        val version: String?
)
