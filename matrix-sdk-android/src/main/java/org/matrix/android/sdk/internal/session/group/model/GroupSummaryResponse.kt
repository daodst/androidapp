

package org.matrix.android.sdk.internal.session.group.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class GroupSummaryResponse(
        
        @Json(name = "profile") val profile: GroupProfile? = null,

        
        @Json(name = "users_section") val usersSection: GroupSummaryUsersSection? = null,

        
        @Json(name = "user") val user: GroupSummaryUser? = null,

        
        @Json(name = "rooms_section") val roomsSection: GroupSummaryRoomsSection? = null
)
