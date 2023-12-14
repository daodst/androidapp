

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.events.model.Event


@JsonClass(generateAdapter = true)
data class RoomSyncUnreadNotifications(
        
        val update: Boolean = true,
        
        @Json(name = "events") val events: List<Event>? = null,

        
        @Json(name = "notification_count") val notificationCount: Int? = null,

        
        @Json(name = "highlight_count") val highlightCount: Int? = null)
