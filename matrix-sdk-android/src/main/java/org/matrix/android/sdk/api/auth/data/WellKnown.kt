

package org.matrix.android.sdk.api.auth.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict


@JsonClass(generateAdapter = true)
data class WellKnown(
        @Json(name = "m.homeserver")
        val homeServer: WellKnownBaseConfig? = null,

        @Json(name = "m.identity_server")
        val identityServer: WellKnownBaseConfig? = null,

        @Json(name = "m.integrations")
        val integrations: JsonDict? = null
)
