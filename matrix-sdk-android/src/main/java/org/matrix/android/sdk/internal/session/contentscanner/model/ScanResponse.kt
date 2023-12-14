

package org.matrix.android.sdk.internal.session.contentscanner.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class ScanResponse(
        @Json(name = "clean") val clean: Boolean,
        
        @Json(name = "info") val info: String?
)
