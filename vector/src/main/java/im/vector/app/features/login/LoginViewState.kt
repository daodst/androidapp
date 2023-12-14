

package im.vector.app.features.login

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.PersistState
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized

data class LoginViewState(
        val asyncLoginAction: Async<Unit> = Uninitialized,
        val asyncHomeServerLoginFlowRequest: Async<Unit> = Uninitialized,
        val asyncResetPassword: Async<Unit> = Uninitialized,
        val asyncResetMailConfirmed: Async<Unit> = Uninitialized,
        val asyncRegistration: Async<Unit> = Uninitialized,

        
        @PersistState
        val serverType: ServerType = ServerType.Unknown,
        @PersistState
        val signMode: SignMode = SignMode.Unknown,
        @PersistState
        val resetPasswordEmail: String? = null,
        @PersistState
        val homeServerUrlFromUser: String? = null,

        
        @PersistState
        val homeServerUrl: String? = null,

        
        @PersistState
        val deviceId: String? = null,

        
        @PersistState
        val loginMode: LoginMode = LoginMode.Unknown,
        
        @PersistState
        val loginModeSupportedTypes: List<String> = emptyList(),
        val knownCustomHomeServersUrls: List<String> = emptyList()
) : MavericksState {

    fun isLoading(): Boolean {
        return asyncLoginAction is Loading ||
                asyncHomeServerLoginFlowRequest is Loading ||
                asyncResetPassword is Loading ||
                asyncResetMailConfirmed is Loading ||
                asyncRegistration is Loading ||
                
                asyncLoginAction is Success
    }

    fun isUserLogged(): Boolean {
        return asyncLoginAction is Success
    }
}
