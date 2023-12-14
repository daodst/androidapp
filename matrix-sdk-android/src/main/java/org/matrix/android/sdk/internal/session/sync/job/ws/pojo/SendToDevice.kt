package org.matrix.android.sdk.internal.session.sync.job.ws.pojo

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.api.session.sync.model.ToDeviceSyncResponse
import java.lang.reflect.Type

private fun BaseResponse<Event>.asSyncResponse(): SyncResponse {

    val events = mutableListOf<Event>()
    events.add(body)
    
    return SyncResponse(canUse = true,    position = position,toDevice = ToDeviceSyncResponse(events = events))
}

@JsonClass(generateAdapter = true)
class SendToDevice() {

    companion object {

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, Event::class.java)
        }

        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            return moshi.adapter<BaseResponse<Event>>(getBaseType()).fromJson(data)?.asSyncResponse()
        }
    }
}
