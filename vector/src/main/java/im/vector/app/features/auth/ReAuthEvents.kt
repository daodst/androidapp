

package im.vector.app.features.auth

import im.vector.app.core.platform.VectorViewEvents

sealed class ReAuthEvents : VectorViewEvents {
    data class OpenSsoURl(val url: String) : ReAuthEvents()
    object Dismiss : ReAuthEvents()
    data class PasswordFinishSuccess(val passwordSafeForIntent: String) : ReAuthEvents()
}
