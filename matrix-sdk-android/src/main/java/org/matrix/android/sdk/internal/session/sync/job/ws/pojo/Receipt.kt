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
import java.lang.reflect.Type

private fun BaseResponse<Receipt>.asSyncResponse(): SyncResponse {
    

    val timestamp = mapOf("ts" to body.timestamp)

    val uid = mapOf(body.userId to timestamp)

    val read = mapOf("m.read" to uid)

    val event = mapOf(body.eventId to read)

    val content = mapOf("content" to event)

    val events = mutableListOf<Event>()
    events.add(
            Event(
                    type = EventType.RECEIPT,
                    roomId = body.roomId,
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
    
    return SyncResponse(canUse = true,   position = position, rooms = RoomsSyncResponse(join = join))
}


@JsonClass(generateAdapter = true)
class Receipt(
        @Json(name = "user_id")
        val userId: String,

        @Json(name = "room_id")
        val roomId: String,

        @Json(name = "event_id")
        val eventId: String,

        @Json(name = "type")
        val type: String,

        @Json(name = "timestamp")
        val timestamp: Long,
) {

    companion object {

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, Receipt::class.java)
        }

        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            return moshi.adapter<BaseResponse<Receipt>>(getBaseType()).fromJson(data)?.asSyncResponse()
        }
    }
}
