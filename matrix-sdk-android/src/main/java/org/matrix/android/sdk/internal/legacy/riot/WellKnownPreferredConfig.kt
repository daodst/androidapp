
package org.matrix.android.sdk.internal.legacy.riot

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class WellKnownPreferredConfig {

    @JvmField
    @Json(name = "preferredDomain")
    var preferredDomain: String? = null
}
