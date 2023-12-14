

package org.matrix.android.sdk.internal.session.presence.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.presence.model.PresenceEnum

@JsonClass(generateAdapter = true)
internal data class GetPresenceResponse(
        @Json(name = "presence")
        val presence: PresenceEnum,
        @Json(name = "last_active_ago")
        val lastActiveAgo: Long? = null,
        @Json(name = "status_msg")
        val message: String? = null,
        @Json(name = "currently_active")
        val isCurrentlyActive: Boolean? = null
)
