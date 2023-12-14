

package im.vector.app.features.signout.soft

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.auth.data.Credentials

sealed class SoftLogoutAction : VectorViewModelAction {
    
    object RetryLoginFlow : SoftLogoutAction()

    
    data class PasswordChanged(val password: String) : SoftLogoutAction()
    data class SignInAgain(val password: String) : SoftLogoutAction()

    
    data class WebLoginSuccess(val credentials: Credentials) : SoftLogoutAction()

    
    object ClearData : SoftLogoutAction()
}
