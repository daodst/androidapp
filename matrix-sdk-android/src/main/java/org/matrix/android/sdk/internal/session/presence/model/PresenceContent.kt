

package org.matrix.android.sdk.internal.session.presence.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.presence.model.PresenceEnum


@JsonClass(generateAdapter = true)
internal data class PresenceContent(
        
        @Json(name = "presence") val presence: PresenceEnum,
        
        @Json(name = "last_active_ago") val lastActiveAgo: Long? = null,
        
        @Json(name = "status_msg") val statusMessage: String? = null,
        
        @Json(name = "currently_active") val isCurrentlyActive: Boolean = false,
        
        @Json(name = "avatar_url") val avatarUrl: String? = null,
        
        @Json(name = "displayname") val displayName: String? = null
)
