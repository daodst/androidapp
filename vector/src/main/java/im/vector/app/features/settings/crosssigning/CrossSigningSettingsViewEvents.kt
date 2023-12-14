

package im.vector.app.features.settings.crosssigning

import im.vector.app.core.platform.VectorViewEvents
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse


sealed class CrossSigningSettingsViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : CrossSigningSettingsViewEvents()
    data class RequestReAuth(val registrationFlowResponse: RegistrationFlowResponse, val lastErrorCode: String?) : CrossSigningSettingsViewEvents()
    data class ShowModalWaitingView(val status: String?) : CrossSigningSettingsViewEvents()
    object HideModalWaitingView : CrossSigningSettingsViewEvents()
}
