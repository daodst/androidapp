

package org.matrix.android.sdk.api.session.space.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SpaceParentContent(
        
        @Json(name = "via") val via: List<String>? = null,
        
        @Json(name = "canonical") val canonical: Boolean? = false
)
