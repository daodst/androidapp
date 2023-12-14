
package org.matrix.android.sdk.api.session.room.model.roomdirectory

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PublicRoomsFilter(
        
        @Json(name = "generic_search_term")
        val searchTerm: String? = null
)
