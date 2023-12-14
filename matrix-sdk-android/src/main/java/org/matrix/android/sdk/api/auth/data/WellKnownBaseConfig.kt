

package org.matrix.android.sdk.api.auth.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class WellKnownBaseConfig(
        @Json(name = "base_url")
        val baseURL: String? = null
)
