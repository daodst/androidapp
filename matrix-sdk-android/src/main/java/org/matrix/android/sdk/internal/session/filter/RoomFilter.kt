
package org.matrix.android.sdk.internal.session.filter

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class RoomFilter(
        
        @Json(name = "not_rooms") val notRooms: List<String>? = null,
        
        @Json(name = "rooms") val rooms: List<String>? = null,
        
        @Json(name = "ephemeral") val ephemeral: RoomEventFilter? = null,
        
        @Json(name = "include_leave") val includeLeave: Boolean? = null,
        
        @Json(name = "state") val state: RoomEventFilter? = null,
        
        @Json(name = "timeline") val timeline: RoomEventFilter? = null,
        
        @Json(name = "account_data") val accountData: RoomEventFilter? = null
) {

    fun hasData(): Boolean {
        return (notRooms != null ||
                rooms != null ||
                ephemeral != null ||
                includeLeave != null ||
                state != null ||
                timeline != null ||
                accountData != null)
    }
}
