

package org.matrix.android.sdk.internal.auth.login

import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.auth.registration.AddThreePidRegistrationResponse


@JsonClass(generateAdapter = true)
internal data class ResetPasswordData(
        val newPassword: String,
        val addThreePidRegistrationResponse: AddThreePidRegistrationResponse
)
