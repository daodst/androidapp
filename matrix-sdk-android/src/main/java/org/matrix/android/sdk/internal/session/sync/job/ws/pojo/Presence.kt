package org.matrix.android.sdk.internal.session.sync.job.ws.pojo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.sync.model.PresenceSyncResponse
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.api.session.user.model.User
import java.lang.reflect.Type




private fun BaseResponse<Presence>.asSyncResponse(): SyncResponse {

    val content = mapOf("last_active_ago" to body.ts, "presence" to body.presence)
    val events = mutableListOf<Event>()
    events.add(
            Event(
                    type = EventType.PRESENCE,
                    senderId = body.userId,
                    content = content,
            )
    )
    
    return SyncResponse(
            canUse = true,
            position = position,
            presence = PresenceSyncResponse(events = events),
    )
}

@JsonClass(generateAdapter = true)
class Presence(
        @Json(name = "presence")
        val presence: String,

        @Json(name = "status_msg")
        val statusMsg: String,

        @Json(name = "ts")
        val ts: Long,

        @Json(name = "user_id")
        val userId: String,
) {
    companion object {

        
        val json = """    {
                "cmd": "PRESENCE", 
                "seq": 27,         
                "version": "0.0.1",
                "position": 77466, 
                "body": {          
                    "presence": "online",
                    "status_msg": "",
                    "ts": 1689733832428,
                    "user_id": "@dst1utchedexufj44p2q52wntgp7g4mstm2ppkvy6w:1111.com"
                      }
                 }"""

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, Presence::class.java)
        }

        
        
        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            return moshi.adapter<BaseResponse<Presence>>(getBaseType()).fromJson(data)?.asSyncResponse()
        }
    }
}
