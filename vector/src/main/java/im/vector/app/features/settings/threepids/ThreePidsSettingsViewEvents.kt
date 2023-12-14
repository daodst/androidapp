

package im.vector.app.features.settings.threepids

import im.vector.app.core.platform.VectorViewEvents
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse

sealed class ThreePidsSettingsViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : ThreePidsSettingsViewEvents()

    
    data class RequestReAuth(val registrationFlowResponse: RegistrationFlowResponse, val lastErrorCode: String?) : ThreePidsSettingsViewEvents()
}
