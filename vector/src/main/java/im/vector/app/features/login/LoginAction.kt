

package im.vector.app.features.login

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.auth.data.SsoIdentityProvider
import org.matrix.android.sdk.api.auth.registration.RegisterThreePid
import org.matrix.android.sdk.api.network.ssl.Fingerprint

sealed class LoginAction : VectorViewModelAction {
    data class OnGetStarted(val resetLoginConfig: Boolean) : LoginAction()

    data class UpdateServerType(val serverType: ServerType) : LoginAction()
    data class UpdateHomeServer(val homeServerUrl: String) : LoginAction()
    data class UpdateSignMode(val signMode: SignMode) : LoginAction()
    data class LoginWithToken(val loginToken: String) : LoginAction()
    data class WebLoginSuccess(val credentials: Credentials) : LoginAction()
    data class InitWith(val loginConfig: LoginConfig?) : LoginAction()
    data class ResetPassword(val email: String, val newPassword: String) : LoginAction()
    object ResetPasswordMailConfirmed : LoginAction()

    
    data class LoginOrRegister(val username: String, val password: String, val initialDeviceName: String) : LoginAction()

    
    open class RegisterAction : LoginAction()

    data class AddThreePid(val threePid: RegisterThreePid) : RegisterAction()
    object SendAgainThreePid : RegisterAction()

    
    data class ValidateThreePid(val code: String) : RegisterAction()

    data class CheckIfEmailHasBeenValidated(val delayMillis: Long) : RegisterAction()
    object StopEmailValidationCheck : RegisterAction()

    data class CaptchaDone(val captchaResponse: String) : RegisterAction()
    object AcceptTerms : RegisterAction()
    object RegisterDummy : RegisterAction()

    
    open class ResetAction : LoginAction()

    object ResetHomeServerType : ResetAction()
    object ResetHomeServerUrl : ResetAction()
    object ResetSignMode : ResetAction()
    object ResetLogin : ResetAction()
    object ResetResetPassword : ResetAction()

    
    object ClearHomeServerHistory : LoginAction()

    
    data class SetupSsoForSessionRecovery(val homeServerUrl: String,
                                          val deviceId: String,
                                          val ssoIdentityProviders: List<SsoIdentityProvider>?) : LoginAction()

    data class PostViewEvent(val viewEvent: LoginViewEvents) : LoginAction()

    data class UserAcceptCertificate(val fingerprint: Fingerprint) : LoginAction()
}
