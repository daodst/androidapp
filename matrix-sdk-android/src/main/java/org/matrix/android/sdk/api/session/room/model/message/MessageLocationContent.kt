

package org.matrix.android.sdk.api.session.room.model.message

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class MessageLocationContent(
        
        @Json(name = MessageContent.MSG_TYPE_JSON_KEY) override val msgType: String = MessageType.MSGTYPE_LOCATION,

        
        @Json(name = "body") override val body: String,

        
        @Json(name = "geo_uri") val geoUri: String,

        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,
        
        @Json(name = "org.matrix.msc3488.location") val unstableLocationInfo: LocationInfo? = null,
        @Json(name = "m.location") val locationInfo: LocationInfo? = null,
        
        @Json(name = "org.matrix.msc3488.ts") val unstableTs: Long? = null,
        @Json(name = "m.ts") val ts: Long? = null,
        @Json(name = "org.matrix.msc1767.text") val unstableText: String? = null,
        @Json(name = "m.text") val text: String? = null,
        
        @Json(name = "org.matrix.msc3488.asset") val unstableLocationAsset: LocationAsset? = null,
        @Json(name = "m.asset") val locationAsset: LocationAsset? = null
) : MessageContent {

    fun getBestLocationInfo() = locationInfo ?: unstableLocationInfo

    fun getBestTs() = ts ?: unstableTs

    fun getBestText() = text ?: unstableText

    fun getBestLocationAsset() = locationAsset ?: unstableLocationAsset

    fun getBestGeoUri() = getBestLocationInfo()?.geoUri ?: geoUri

    
    fun isSelfLocation(): Boolean {
        
        val locationAsset = getBestLocationAsset()
        return locationAsset?.type == null || locationAsset.type == LocationAssetType.SELF
    }
}
