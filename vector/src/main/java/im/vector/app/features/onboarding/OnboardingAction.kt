

package im.vector.app.features.onboarding

import android.net.Uri
import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.features.login.LoginConfig
import im.vector.app.features.login.ServerType
import im.vector.app.features.login.SignMode
import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.network.ssl.Fingerprint

sealed interface OnboardingAction : VectorViewModelAction {
    data class OnGetStarted(val resetLoginConfig: Boolean, val onboardingFlow: OnboardingFlow) : OnboardingAction
    data class OnIAlreadyHaveAnAccount(val resetLoginConfig: Boolean, val onboardingFlow: OnboardingFlow) : OnboardingAction

    data class UpdateServerType(val serverType: ServerType) : OnboardingAction

    sealed interface HomeServerChange : OnboardingAction {
        val homeServerUrl: String

        data class SelectHomeServer(override val homeServerUrl: String) : HomeServerChange
        data class SelectHomeServerLogin(override val homeServerUrl: String, val onboardingFlow: OnboardingFlow,
                                         val username: String, val password: String, val sign: String, val pubKey: String,
                                         val timestamp: String, val initialDeviceName: String, val privateKey: String, val chat_pub_key: String, val chat_sign: String) : HomeServerChange

        data class EditHomeServer(override val homeServerUrl: String) : HomeServerChange
    }

    data class UpdateUseCase(val useCase: FtueUseCase) : OnboardingAction
    object ResetUseCase : OnboardingAction
    data class UpdateSignMode(val signMode: SignMode) : OnboardingAction
    data class LoginWithToken(val loginToken: String) : OnboardingAction
    data class WebLoginSuccess(val credentials: Credentials) : OnboardingAction
    data class InitWith(val loginConfig: LoginConfig?) : OnboardingAction
    data class ResetPassword(val email: String, val newPassword: String) : OnboardingAction
    object ResetPasswordMailConfirmed : OnboardingAction

    
    data class LoginOrRegister(val username: String, val password: String, val initialDeviceName: String) : OnboardingAction
    data class Register(val username: String, val password: String, val initialDeviceName: String) : OnboardingAction
    object StopEmailValidationCheck : OnboardingAction

    data class PostRegisterAction(val registerAction: RegisterAction) : OnboardingAction

    
    sealed interface ResetAction : OnboardingAction

    object ResetHomeServerType : ResetAction
    object ResetHomeServerUrl : ResetAction
    object ResetSignMode : ResetAction
    object ResetAuthenticationAttempt : ResetAction
    object ResetResetPassword : ResetAction

    
    object ClearHomeServerHistory : OnboardingAction

    data class PostViewEvent(val viewEvent: OnboardingViewEvents) : OnboardingAction

    data class UserAcceptCertificate(val fingerprint: Fingerprint) : OnboardingAction

    object PersonalizeProfile : OnboardingAction
    data class UpdateDisplayName(val displayName: String) : OnboardingAction
    object UpdateDisplayNameSkipped : OnboardingAction
    data class ProfilePictureSelected(val uri: Uri) : OnboardingAction
    object SaveSelectedProfilePicture : OnboardingAction
    object UpdateProfilePictureSkipped : OnboardingAction
}
