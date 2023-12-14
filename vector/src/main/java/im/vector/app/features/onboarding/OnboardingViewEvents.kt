

package im.vector.app.features.onboarding

import im.vector.app.core.platform.VectorViewEvents
import im.vector.app.features.login.ServerType
import im.vector.app.features.login.SignMode
import org.matrix.android.sdk.api.auth.registration.FlowResult


sealed class OnboardingViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : OnboardingViewEvents()
    data class Failure(val throwable: Throwable) : OnboardingViewEvents()

    data class RegistrationFlowResult(val flowResult: FlowResult, val isRegistrationStarted: Boolean) : OnboardingViewEvents()
    object OutdatedHomeserver : OnboardingViewEvents()

    

    object OpenUseCaseSelection : OnboardingViewEvents()
    object OpenServerSelection : OnboardingViewEvents()
    object OpenCombinedRegister : OnboardingViewEvents()
    object EditServerSelection : OnboardingViewEvents()
    data class OnServerSelectionDone(val serverType: ServerType) : OnboardingViewEvents()
    object OnLoginFlowRetrieved : OnboardingViewEvents()
    object OnHomeserverEdited : OnboardingViewEvents()
    data class OnSignModeSelected(val signMode: SignMode) : OnboardingViewEvents()
    object OnForgetPasswordClicked : OnboardingViewEvents()
    object OnResetPasswordSendThreePidDone : OnboardingViewEvents()
    object OnResetPasswordMailConfirmationSuccess : OnboardingViewEvents()
    object OnResetPasswordMailConfirmationSuccessDone : OnboardingViewEvents()

    data class OnSendEmailSuccess(val email: String) : OnboardingViewEvents()
    data class OnSendMsisdnSuccess(val msisdn: String) : OnboardingViewEvents()

    data class OnWebLoginError(val errorCode: Int, val description: String, val failingUrl: String) : OnboardingViewEvents()
    object OnAccountCreated : OnboardingViewEvents()

    
    data class OnAccountSignedIn(val privateKey: String, val walletPwd: String) : OnboardingViewEvents()

    object OnTakeMeHome : OnboardingViewEvents()
    object OnChooseDisplayName : OnboardingViewEvents()
    object OnChooseProfilePicture : OnboardingViewEvents()
    object OnPersonalizationComplete : OnboardingViewEvents()
    object OnBack : OnboardingViewEvents()
}
