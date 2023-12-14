

package im.vector.app.features.settings.account.deactivation

import im.vector.app.core.platform.VectorViewModelAction

sealed class DeactivateAccountAction : VectorViewModelAction {
    data class DeactivateAccount(val eraseAllData: Boolean) : DeactivateAccountAction()

    object SsoAuthDone : DeactivateAccountAction()
    data class PasswordAuthDone(val password: String) : DeactivateAccountAction()
    object ReAuthCancelled : DeactivateAccountAction()
}
