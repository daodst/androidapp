

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class MessageCusChartContent(
        
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String,

        
        @Json(name = "body") override val body: String,

        
        @Json(name = "format") override val format: String? = null,

        
        @Json(name = "formatted_body") override val formattedBody: String? = null,

        @Json(name = "deviceRate") val
        deviceRate: String = "",
        @Json(name = "deviceRateSmall") val
        deviceRateSmall: String = "",
        @Json(name = "deviceRateUp") val
        deviceRateUp: Boolean = false,
        @Json(name = "connRate") val
        connRate: String = "",
        @Json(name = "connRateSmall") val
        connRateSmall: String = "",
        @Json(name = "connRateUp") val
        connRateUp: Boolean = false,
        @Json(name = "newDeviceNum") val
        newDeviceNum: String = "",
        @Json(name = "dvmNum") val
        dvmNum: String = "",
        @Json(name = "posNum") val
        posNum: String = "",
        @Json(name = "day3Pos") val
        day3Pos: String = "",
        @Json(name = "day3Active") val
        day3Active: String = "",
        @Json(name = "day3Lg") val
        day3Lg: String = "",
        @Json(name = "day7Pos") val
        day7Pos: String = "",
        @Json(name = "day7Active") val
        day7Active: String = "",
        @Json(name = "day7Lg") val
        day7Lg: String = "",
        @Json(name = "file") val
        file: String = "",

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null
) : MessageContentWithFormattedBody
