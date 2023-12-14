
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class DeleteDeviceParams(
        @Json(name = "auth")
        val auth: Map<String, *>? = null
)
