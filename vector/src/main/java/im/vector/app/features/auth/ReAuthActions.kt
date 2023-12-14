

package im.vector.app.features.auth

import im.vector.app.core.platform.VectorViewModelAction

sealed class ReAuthActions : VectorViewModelAction {
    object StartSSOFallback : ReAuthActions()
    object FallBackPageLoaded : ReAuthActions()
    object FallBackPageClosed : ReAuthActions()
    data class ReAuthWithPass(val password: String) : ReAuthActions()
}
