
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UnbindThreePidResponse(
        @Json(name = "id_server_unbind_result")
        val idServerUnbindResult: String?
) {
    fun isSuccess() = idServerUnbindResult == "success"
}
