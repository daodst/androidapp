
package org.matrix.android.sdk.internal.session.pushers

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class EnabledBody(
        @Json(name = "enabled")
        val enabled: Boolean
)
