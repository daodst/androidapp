package org.matrix.android.sdk.internal.session.sync.job.ws.pojo

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.matrix.android.sdk.api.session.sync.model.DeviceListResponse
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import java.lang.reflect.Type

private fun BaseResponse<String>.asSyncResponse(): SyncResponse {
    val changed = mutableListOf<String>()
    changed.add(body)
    
    return SyncResponse(
            canUse = true,
            position = position,
            deviceLists = DeviceListResponse(changed = changed),
    )
}

class KeyChange() {

    companion object {

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, String::class.java)
        }

        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            return moshi.adapter<BaseResponse<String>>(getBaseType()).fromJson(data)?.asSyncResponse()
        }
    }
}
