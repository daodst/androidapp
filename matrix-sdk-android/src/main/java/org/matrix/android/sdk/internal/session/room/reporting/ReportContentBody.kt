

package org.matrix.android.sdk.internal.session.room.reporting

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ReportContentBody(
        
        @Json(name = "score") val score: Int,

        
        @Json(name = "reason") val reason: String
)
