

package org.matrix.android.sdk.api.session.uia

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class InteractiveAuthenticationFlow(

        @Json(name = "type")
        val type: String? = null,

        @Json(name = "stages")
        val stages: List<String>? = null
)
