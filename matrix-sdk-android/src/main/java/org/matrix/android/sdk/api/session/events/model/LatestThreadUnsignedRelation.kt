
package org.matrix.android.sdk.api.session.events.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LatestThreadUnsignedRelation(
        override val limited: Boolean? = false,
        override val count: Int? = 0,
        @Json(name = "latest_event")
        val event: Event? = null,
        @Json(name = "current_user_participated")
        val isUserParticipating: Boolean? = false

) : UnsignedRelationInfo
