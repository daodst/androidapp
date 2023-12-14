

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent


@JsonClass(generateAdapter = true)
data class MessageEndPollContent(
        @Json(name = "m.relates_to") val relatesTo: RelationDefaultContent? = null
)
