package org.matrix.android.sdk.internal.session.sync.job.ws.pojo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.sync.model.LazyRoomSyncEphemeral
import org.matrix.android.sdk.api.session.sync.model.RoomSync
import org.matrix.android.sdk.api.session.sync.model.RoomSyncEphemeral
import org.matrix.android.sdk.api.session.sync.model.RoomSyncUnreadNotifications
import org.matrix.android.sdk.api.session.sync.model.RoomsSyncResponse
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import timber.log.Timber
import java.lang.reflect.Type

private fun BaseResponse<Typing>.asSyncResponse(): SyncResponse {
    val userIds = mutableListOf<String>()
    
    body.userId?.let {
        if (body.typing) {
            userIds.add(it)
        }
    }
    val content = mapOf("user_ids" to userIds)

    val events = mutableListOf<Event>()
    events.add(
            Event(
                    type = EventType.TYPING,
                    senderId = body.userId,
                    content = content,
            )
    )

    val join = mutableMapOf<String, RoomSync>()

    join[body.roomId] = RoomSync(
            ephemeral = LazyRoomSyncEphemeral.Parsed(
                    RoomSyncEphemeral(events = events)
            ),
            
            unreadNotifications = RoomSyncUnreadNotifications(update = false)
    )
    
    return SyncResponse(canUse = true,    position = position,rooms = RoomsSyncResponse(join = join))
}


@JsonClass(generateAdapter = true) class Typing(
        @Json(name = "room_id") val roomId: String,

        @Json(name = "user_id") val userId: String?,

        @Json(name = "typing") val typing: Boolean = false,

) {
    companion object {

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, Typing::class.java)
        }

        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            Timber.i("----Typing------------------$data---------")
            return moshi.adapter<BaseResponse<Typing>>(getBaseType()).fromJson(data)?.asSyncResponse()
        }
    }
}
