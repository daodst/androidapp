

package org.matrix.android.sdk.api.auth.login

data class LoginProfileInfo(
        val matrixId: String,
        val displayName: String?,
        val fullAvatarUrl: String?
)
