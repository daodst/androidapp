

package org.matrix.android.sdk.api.auth.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DiscoveryInformation(
        
        @Json(name = "m.homeserver")
        val homeServer: WellKnownBaseConfig? = null,

        
        @Json(name = "m.identity_server")
        val identityServer: WellKnownBaseConfig? = null
)
