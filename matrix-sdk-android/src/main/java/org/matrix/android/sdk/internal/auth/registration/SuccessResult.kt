

package org.matrix.android.sdk.internal.auth.registration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.extensions.orFalse

@JsonClass(generateAdapter = true)
internal data class SuccessResult(
        @Json(name = "success")
        val success: Boolean?
) {
    fun isSuccess() = success.orFalse()
}
