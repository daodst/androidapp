

package org.matrix.android.sdk.api.session.room.model.relation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReactionContent(
        @Json(name = "m.relates_to") val relatesTo: ReactionInfo? = null
)
