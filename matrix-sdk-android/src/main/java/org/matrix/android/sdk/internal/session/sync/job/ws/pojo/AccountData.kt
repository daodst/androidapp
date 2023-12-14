package org.matrix.android.sdk.internal.session.sync.job.ws.pojo

import android.text.TextUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONException
import org.matrix.android.sdk.api.session.accountdata.UserAccountDataEvent
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.sync.model.RoomSync
import org.matrix.android.sdk.api.session.sync.model.RoomSyncAccountData
import org.matrix.android.sdk.api.session.sync.model.RoomSyncUnreadNotifications
import org.matrix.android.sdk.api.session.sync.model.RoomsSyncResponse
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.api.session.sync.model.UserAccountDataSync
import java.lang.reflect.Type

private fun BaseResponse<Content>.asSyncResponse(moshi: Moshi): SyncResponse {

    val roomId = try {
        body["room_id"] as? String
    } catch (e: JSONException) {
        ""
    }
    
    if (TextUtils.isEmpty(roomId)) {
        val events = mutableListOf<UserAccountDataEvent>()
        moshi.adapter(UserAccountDataEvent::class.java).fromJsonValue(body)?.let {
            
            events.add(it)
        }
        return SyncResponse(canUse = true,   position = position, accountData = UserAccountDataSync(list = events))
    } else {
        val join = mutableMapOf<String, RoomSync>()
        val events = mutableListOf<Event>()
        moshi.adapter(Event::class.java).fromJsonValue(body)?.let { events.add(it) }
        join[roomId!!] = RoomSync(
                accountData = RoomSyncAccountData(
                        events = events
                ),
                
                unreadNotifications = RoomSyncUnreadNotifications(update = false)
        )
        return SyncResponse(
                canUse = true,
                position = position,
                rooms = RoomsSyncResponse(join = join)
        )
    }
}

class AccountData(
        
) {

    companion object {

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, Map::class.java)
        }

        
        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            return moshi.adapter<BaseResponse<Content>>(getBaseType()).fromJson(data)?.asSyncResponse(moshi)
        }
    }
}
