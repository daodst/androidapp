

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomSyncSummary(

        
        @Json(name = "m.heroes") val heroes: List<String> = emptyList(),

        
        @Json(name = "m.joined_member_count") val joinedMembersCount: Int? = null,

        
        @Json(name = "m.invited_member_count") val invitedMembersCount: Int? = null
)
