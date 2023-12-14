

package im.vector.app.features.signout.soft

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import im.vector.app.features.login.LoginMode

data class SoftLogoutViewState(
        val asyncHomeServerLoginFlowRequest: Async<LoginMode> = Uninitialized,
        val asyncLoginAction: Async<Unit> = Uninitialized,
        val homeServerUrl: String,
        val userId: String,
        val deviceId: String,
        val userDisplayName: String,
        val hasUnsavedKeys: Boolean,
        val enteredPassword: String = ""
) : MavericksState {

    fun isLoading(): Boolean {
        return asyncLoginAction is Loading ||
                
                asyncLoginAction is Success
    }
}
