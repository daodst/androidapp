package org.matrix.android.sdk.internal.session.sync.job.ws.pojo

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import java.lang.reflect.Type

private fun BaseResponse<RetireNewInvite>.asSyncResponse(): SyncResponse {
    
    return SyncResponse(canUse = true, position = position)
}

@JsonClass(generateAdapter = true)
class RetireNewInvite() {

    companion object {

        private fun getBaseType(): Type {
            return Types.newParameterizedType(BaseResponse::class.java, RetireNewInvite::class.java)
        }

        fun getSyncResponse(moshi: Moshi, data: String): SyncResponse? {
            return moshi.adapter<BaseResponse<RetireNewInvite>>(getBaseType()).fromJson(data)?.asSyncResponse()
        }
    }
}
