package org.matrix.android.sdk.internal.session.sync.job.ws.pojo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.matrix.android.sdk.api.session.sync.model.RoomSync
import org.matrix.android.sdk.api.session.sync.model.RoomSyncUnreadNotifications
import org.matrix.android.sdk.api.session.sync.model.RoomsSyncResponse
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import java.lang.reflect.Type


private fun BaseResponse<NotificationData>.asSyncResponse(): SyncResponse {

    val join = mutableMapOf<String, RoomSync>()
    join[body.roomId] = RoomSync(
            unreadNotifications = RoomSyncUnreadNotifications(notificationCount = body.notificationCount, highlightCount = body.highlightCount)
    )
    
    return SyncResponse(
            canUse = true,
            position = position,
            rooms = RoomsSyncResponse(join = join)
    )
}


@JsonClass(generateAdapter = true)
class NotificationData(
        @Json(name = "unread_highlight_count")
        val highlightCount: Int,
        @Json(name = "unread_notification_count")
        val notificationCount: Int,
        @Json(name = "room_id")
        val roomId: String,
) {

    companion object {

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, NotificationData::class.java)
        }

        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            return moshi.adapter<BaseResponse<NotificationData>>(getBaseType()).fromJson(data)?.asSyncResponse()
        }
    }
}
