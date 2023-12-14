

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RoomSync(
        
        @Json(name = "state") val state: RoomSyncState? = null,

        
        @Json(name = "timeline") val timeline: RoomSyncTimeline? = null,

        
        @Json(name = "ephemeral") val ephemeral: LazyRoomSyncEphemeral? = null,

        
        @Json(name = "account_data") val accountData: RoomSyncAccountData? = null,

        
        @Json(name = "unread_notifications") val unreadNotifications: RoomSyncUnreadNotifications? = null,

        
        @Json(name = "summary") val summary: RoomSyncSummary? = null

)
