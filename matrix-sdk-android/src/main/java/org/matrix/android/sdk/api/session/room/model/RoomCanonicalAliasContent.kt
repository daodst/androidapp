

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoomCanonicalAliasContent(
        
        @Json(name = "alias") val canonicalAlias: String? = null,

        
        @Json(name = "alt_aliases") val alternativeAliases: List<String>? = null
)
