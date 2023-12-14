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

private fun BaseResponse<NewEvent>.asSyncResponse(): SyncResponse {

    val roomSync = mutableMapOf<String, RoomSync>()

    roomSync[body.roomId] = body.data.copy(
            
            unreadNotifications = RoomSyncUnreadNotifications(update = false)
    )
    
    if ("join" == body.type) {
        return SyncResponse(canUse = true,   position = position, rooms = RoomsSyncResponse(join = roomSync))
    } else {
        return SyncResponse(canUse = true,    position = position,rooms = RoomsSyncResponse(leave = roomSync))
    }
}


@JsonClass(generateAdapter = true)
class NewEvent(
        @Json(name = "jr") val data: RoomSync,
        @Json(name = "pos") val type: String,
        @Json(name = "room_id") val roomId: String,
) {

    companion object {

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, NewEvent::class.java)
        }

        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            return moshi.adapter<BaseResponse<NewEvent>>(getBaseType()).fromJson(data)?.asSyncResponse()
        }
    }
}
