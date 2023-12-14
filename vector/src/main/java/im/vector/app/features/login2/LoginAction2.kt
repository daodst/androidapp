

package im.vector.app.features.login2

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.features.login.LoginConfig
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.auth.data.SsoIdentityProvider
import org.matrix.android.sdk.api.auth.registration.RegisterThreePid
import org.matrix.android.sdk.api.network.ssl.Fingerprint

sealed class LoginAction2 : VectorViewModelAction {
    
    data class UpdateSignMode(val signMode: SignMode2) : LoginAction2()

    
    object ChooseAServerForSignin : LoginAction2()

    object EnterServerUrl : LoginAction2()
    object ChooseDefaultHomeServer : LoginAction2()
    data class UpdateHomeServer(val homeServerUrl: String) : LoginAction2()
    data class LoginWithToken(val loginToken: String) : LoginAction2()
    data class WebLoginSuccess(val credentials: Credentials) : LoginAction2()
    data class InitWith(val loginConfig: LoginConfig?) : LoginAction2()
    data class ResetPassword(val email: String, val newPassword: String) : LoginAction2()
    object ResetPasswordMailConfirmed : LoginAction2()

    
    data class SetUserName(val username: String) : LoginAction2()

    
    data class SetUserPassword(val password: String) : LoginAction2()

    
    data class LoginWith(val login: String, val password: String) : LoginAction2()

    
    open class RegisterAction : LoginAction2()

    data class AddThreePid(val threePid: RegisterThreePid) : RegisterAction()
    object SendAgainThreePid : RegisterAction()

    
    data class ValidateThreePid(val code: String) : RegisterAction()

    data class CheckIfEmailHasBeenValidated(val delayMillis: Long) : RegisterAction()
    object StopEmailValidationCheck : RegisterAction()

    data class CaptchaDone(val captchaResponse: String) : RegisterAction()
    object AcceptTerms : RegisterAction()
    object RegisterDummy : RegisterAction()

    
    open class ResetAction : LoginAction2()

    object ResetHomeServerUrl : ResetAction()
    object ResetSignMode : ResetAction()
    object ResetSignin : ResetAction()
    object ResetSignup : ResetAction()
    object ResetResetPassword : ResetAction()

    
    object ClearHomeServerHistory : LoginAction2()

    
    data class SetupSsoForSessionRecovery(val homeServerUrl: String,
                                          val deviceId: String,
                                          val ssoIdentityProviders: List<SsoIdentityProvider>?) : LoginAction2()

    data class PostViewEvent(val viewEvent: LoginViewEvents2) : LoginAction2()

    data class UserAcceptCertificate(val fingerprint: Fingerprint) : LoginAction2()

    
    object Finish : LoginAction2()
}
