

package org.matrix.android.sdk.internal.session.account

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.UIABaseAuth

@JsonClass(generateAdapter = true)
internal data class DeactivateAccountParams(
        
        @Json(name = "erase")
        val erase: Boolean,

        @Json(name = "auth")
        val auth: Map<String, *>? = null
) {
    companion object {
        fun create(auth: UIABaseAuth?, erase: Boolean): DeactivateAccountParams {
            return DeactivateAccountParams(
                    auth = auth?.asMap(),
                    erase = erase
            )
        }
    }
}
