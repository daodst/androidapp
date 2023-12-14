

package org.matrix.android.sdk.internal.session.account

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.UserPasswordAuth


@JsonClass(generateAdapter = true)
internal data class ChangePasswordParams(
        @Json(name = "auth")
        val auth: UserPasswordAuth? = null,

        @Json(name = "new_password")
        val newPassword: String? = null
) {
    companion object {
        fun create(userId: String, oldPassword: String, newPassword: String): ChangePasswordParams {
            return ChangePasswordParams(
                    auth = UserPasswordAuth(user = userId, password = oldPassword),
                    newPassword = newPassword
            )
        }
    }
}
