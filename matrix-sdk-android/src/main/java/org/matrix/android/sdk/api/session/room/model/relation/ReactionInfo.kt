

package org.matrix.android.sdk.api.session.room.model.relation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReactionInfo(
        @Json(name = "rel_type") override val type: String?,
        @Json(name = "event_id") override val eventId: String,
        @Json(name = "key") val key: String,
        
        @Json(name = "m.in_reply_to") override val inReplyTo: ReplyToContent? = null,
        @Json(name = "option") override val option: Int? = null,
        @Json(name = "is_falling_back") override val isFallingBack: Boolean? = null
) : RelationContent
