package org.matrix.android.sdk.internal.session.sync.job.ws.pojo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.matrix.android.sdk.api.session.sync.model.InvitedRoomSync
import org.matrix.android.sdk.api.session.sync.model.RoomsSyncResponse
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import java.lang.reflect.Type

private fun BaseResponse<NewInvite>.asSyncResponse(): SyncResponse {

    val invite = mutableMapOf<String, InvitedRoomSync>()
    invite[body.roomId] = body.invite

    
    return SyncResponse(canUse = true,   position = position, rooms = RoomsSyncResponse(invite = invite))
}
@JsonClass(generateAdapter = true)
class NewInvite(

        @Json(name = "invite")
        val invite: InvitedRoomSync,
        @Json(name = "room_id")
        val roomId: String,
) {

    companion object {

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, NewInvite::class.java)
        }

        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            return moshi.adapter<BaseResponse<NewInvite>>(getBaseType()).fromJson(data)?.asSyncResponse()
        }
    }
}
