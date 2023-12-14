
package org.matrix.android.sdk.internal.legacy.riot

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class WellKnownBaseConfig {

    @JvmField
    @Json(name = "base_url")
    var baseURL: String? = null
}
