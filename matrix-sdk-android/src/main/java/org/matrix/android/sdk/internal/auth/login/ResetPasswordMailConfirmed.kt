
package org.matrix.android.sdk.internal.auth.login

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.auth.registration.AuthParams


@JsonClass(generateAdapter = true)
internal data class ResetPasswordMailConfirmed(
        
        @Json(name = "auth")
        val auth: AuthParams? = null,

        
        @Json(name = "new_password")
        val newPassword: String? = null
) {
    companion object {
        fun create(clientSecret: String, sid: String, newPassword: String): ResetPasswordMailConfirmed {
            return ResetPasswordMailConfirmed(
                    auth = AuthParams.createForResetPassword(clientSecret, sid),
                    newPassword = newPassword
            )
        }
    }
}
