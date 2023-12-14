

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class MessageLiveLocationContent(
        
        @Transient
        override val msgType: String = MessageType.MSGTYPE_LIVE_LOCATION,

        @Json(name = "body") override val body: String = "",
        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,

        
        @Json(name = "org.matrix.msc3488.location") val unstableLocationInfo: LocationInfo? = null,
        @Json(name = "m.location") val locationInfo: LocationInfo? = null,

        
        @Json(name = "org.matrix.msc3488.ts") val unstableTimestampAsMilliseconds: Long? = null,
        @Json(name = "m.ts") val timestampAsMilliseconds: Long? = null
) : MessageContent {

    fun getBestLocationInfo() = locationInfo ?: unstableLocationInfo

    fun getBestTimestampAsMilliseconds() = timestampAsMilliseconds ?: unstableTimestampAsMilliseconds
}
