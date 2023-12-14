

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class MessagePollContent(
        
        @Transient
        override val msgType: String = MessageType.MSGTYPE_POLL_START,
        @Json(name = "body") override val body: String = "",
        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,
        @Json(name = "org.matrix.msc3381.poll.start") val unstablePollCreationInfo: PollCreationInfo? = null,
        @Json(name = "m.poll.start") val pollCreationInfo: PollCreationInfo? = null
) : MessageContent {

    fun getBestPollCreationInfo() = pollCreationInfo ?: unstablePollCreationInfo
}
