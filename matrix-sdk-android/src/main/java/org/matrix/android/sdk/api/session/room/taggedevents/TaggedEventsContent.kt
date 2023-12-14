

package org.matrix.android.sdk.api.session.room.taggedevents

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


typealias TaggedEvent = Map<String, TaggedEventInfo>


typealias TaggedEvents = Map<String, TaggedEvent>


@JsonClass(generateAdapter = true)
data class TaggedEventsContent(
        @Json(name = "tags")
        var tags: TaggedEvents = emptyMap()
) {
    val favouriteEvents
        get() = tags[TAG_FAVOURITE].orEmpty()

    val hiddenEvents
        get() = tags[TAG_HIDDEN].orEmpty()

    fun tagEvent(eventId: String, info: TaggedEventInfo, tag: String) {
        val taggedEvents = tags[tag].orEmpty().plus(eventId to info)
        tags = tags.plus(tag to taggedEvents)
    }

    fun untagEvent(eventId: String, tag: String) {
        val taggedEvents = tags[tag]?.minus(eventId).orEmpty()
        tags = tags.plus(tag to taggedEvents)
    }

    companion object {
        const val TAG_FAVOURITE = "m.favourite"
        const val TAG_HIDDEN = "m.hidden"
    }
}

@JsonClass(generateAdapter = true)
data class TaggedEventInfo(
        @Json(name = "keywords")
        val keywords: List<String>? = null,

        @Json(name = "origin_server_ts")
        val originServerTs: Long? = null,

        @Json(name = "tagged_at")
        val taggedAt: Long? = null
)
