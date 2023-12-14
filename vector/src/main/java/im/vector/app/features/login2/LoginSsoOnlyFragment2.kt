

package im.vector.app.features.login2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.extensions.toReducedUrl
import im.vector.app.databinding.FragmentLoginSsoOnly2Binding
import im.vector.app.features.login.SSORedirectRouterActivity
import javax.inject.Inject


class LoginSsoOnlyFragment2 @Inject constructor() : AbstractSSOLoginFragment2<FragmentLoginSsoOnly2Binding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginSsoOnly2Binding {
        return FragmentLoginSsoOnly2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        views.loginSignupSigninSubmit.setOnClickListener { submit() }
    }

    private fun setupUi(state: LoginViewState2) {
        views.loginSignupSigninTitle.text = getString(R.string.login_connect_to, state.homeServerUrlFromUser.toReducedUrl())
    }

    private fun submit() = withState(loginViewModel) { state ->
        loginViewModel.getSsoUrl(
                redirectUrl = SSORedirectRouterActivity.VECTOR_REDIRECT_URL,
                deviceId = state.deviceId,
                providerId = null
        )
                ?.let { openInCustomTab(it) }
    }

    override fun resetViewModel() {
        
    }

    override fun updateWithState(state: LoginViewState2) {
        setupUi(state)
    }
}
