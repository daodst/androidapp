

package im.vector.app.features.settings.account.deactivation

import im.vector.app.core.platform.VectorViewEvents
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse


sealed class DeactivateAccountViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : DeactivateAccountViewEvents()
    object InvalidAuth : DeactivateAccountViewEvents()
    data class OtherFailure(val throwable: Throwable) : DeactivateAccountViewEvents()
    object Done : DeactivateAccountViewEvents()
    data class RequestReAuth(val registrationFlowResponse: RegistrationFlowResponse, val lastErrorCode: String?) : DeactivateAccountViewEvents()
}
