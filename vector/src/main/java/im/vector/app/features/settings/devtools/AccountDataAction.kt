

package im.vector.app.features.settings.devtools

import im.vector.app.core.platform.VectorViewModelAction

sealed class AccountDataAction : VectorViewModelAction {
    data class DeleteAccountData(val type: String) : AccountDataAction()
}
