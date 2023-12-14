

package org.matrix.android.sdk.api.session.room.model.livelocation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.message.LocationAsset
import org.matrix.android.sdk.api.session.room.model.message.LocationAssetType
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageLiveLocationContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

@JsonClass(generateAdapter = true)
data class LiveLocationBeaconContent(
        
        @Transient
        override val msgType: String = MessageType.MSGTYPE_LIVE_LOCATION_STATE,

        @Json(name = "body") override val body: String = "",
        @Json(name = "m.relates_to") override val relatesTo: RelationDefaultContent? = null,
        @Json(name = "m.new_content") override val newContent: Content? = null,

        
        @Json(name = "org.matrix.msc3672.beacon_info") val unstableBeaconInfo: BeaconInfo? = null,
        @Json(name = "m.beacon_info") val beaconInfo: BeaconInfo? = null,
        
        @Json(name = "org.matrix.msc3488.ts") val unstableTimestampAsMilliseconds: Long? = null,
        @Json(name = "m.ts") val timestampAsMilliseconds: Long? = null,
        
        @Json(name = "org.matrix.msc3488.asset") val unstableLocationAsset: LocationAsset = LocationAsset(LocationAssetType.SELF),
        @Json(name = "m.asset") val locationAsset: LocationAsset? = null,

        
        var lastLocationContent: MessageLiveLocationContent? = null,

        
        var hasTimedOut: Boolean = false
) : MessageContent {

    fun getBestBeaconInfo() = beaconInfo ?: unstableBeaconInfo

    fun getBestTimestampAsMilliseconds() = timestampAsMilliseconds ?: unstableTimestampAsMilliseconds

    fun getBestLocationAsset() = locationAsset ?: unstableLocationAsset
}
