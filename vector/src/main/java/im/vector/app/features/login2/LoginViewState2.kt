

package im.vector.app.features.login2

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.PersistState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.core.extensions.toReducedUrl
import im.vector.app.features.login.LoginMode
import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.auth.login.LoginProfileInfo

data class LoginViewState2(
        val isLoading: Boolean = false,

        
        @PersistState
        val signMode: SignMode2 = SignMode2.Unknown,
        @PersistState
        val userName: String? = null,
        @PersistState
        val resetPasswordEmail: String? = null,
        @PersistState
        val homeServerUrlFromUser: String? = null,

        
        @PersistState
        val homeServerUrl: String? = null,

        
        @PersistState
        val deviceId: String? = null,

        
        val loginProfileInfo: Async<LoginProfileInfo> = Uninitialized,

        
        @PersistState
        val loginMode: LoginMode = LoginMode.Unknown,

        
        val knownCustomHomeServersUrls: List<String> = emptyList()
) : MavericksState {

    
    fun userIdentifier(): String {
        return if (userName != null && MatrixPatterns.isUserId(userName)) {
            userName
        } else {
            "@$userName:${homeServerUrlFromUser.toReducedUrl()}"
        }
    }
}
