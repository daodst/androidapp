

package org.matrix.android.sdk.internal.session.search.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchRequestCategories(
        
        @Json(name = "room_events")
        val roomEvents: SearchRequestRoomEvents? = null
)
