

package im.vector.app.features.settings.crosssigning

import im.vector.app.core.platform.VectorViewModelAction

sealed class CrossSigningSettingsAction : VectorViewModelAction {
    object InitializeCrossSigning : CrossSigningSettingsAction()
    object SsoAuthDone : CrossSigningSettingsAction()
    data class PasswordAuthDone(val password: String) : CrossSigningSettingsAction()
    object ReAuthCancelled : CrossSigningSettingsAction()
}
