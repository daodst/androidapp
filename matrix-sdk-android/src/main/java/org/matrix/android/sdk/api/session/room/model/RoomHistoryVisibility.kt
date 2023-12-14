

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = false)
enum class RoomHistoryVisibility {
    
    @Json(name = "world_readable") WORLD_READABLE,

    
    @Json(name = "shared") SHARED,

    
    @Json(name = "invited") INVITED,

    
    @Json(name = "joined") JOINED
}
