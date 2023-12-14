
package org.matrix.android.sdk.api.session.room.model.roomdirectory

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PublicRoomsParams(
        
        @Json(name = "limit")
        val limit: Int? = null,

        
        @Json(name = "since")
        val since: String? = null,

        
        @Json(name = "filter")
        val filter: PublicRoomsFilter? = null,

        
        @Json(name = "include_all_networks")
        val includeAllNetworks: Boolean = false,

        
        @Json(name = "third_party_instance_id")
        val thirdPartyInstanceId: String? = null
)
