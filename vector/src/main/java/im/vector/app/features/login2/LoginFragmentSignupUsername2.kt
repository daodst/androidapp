

package im.vector.app.features.login2

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.autofill.HintConstants
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.toReducedUrl
import im.vector.app.databinding.FragmentLoginSignupUsername2Binding
import im.vector.app.features.login.LoginMode
import im.vector.app.features.login.SSORedirectRouterActivity
import im.vector.app.features.login.SocialLoginButtonsView
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.widget.textChanges
import javax.inject.Inject


class LoginFragmentSignupUsername2 @Inject constructor() : AbstractSSOLoginFragment2<FragmentLoginSignupUsername2Binding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginSignupUsername2Binding {
        return FragmentLoginSignupUsername2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSubmitButton()
        setupAutoFill()
        setupSocialLoginButtons()
    }

    private fun setupAutoFill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            views.loginField.setAutofillHints(HintConstants.AUTOFILL_HINT_NEW_USERNAME)
        }
    }

    private fun setupSocialLoginButtons() {
        views.loginSocialLoginButtons.mode = SocialLoginButtonsView.Mode.MODE_SIGN_UP
    }

    private fun submit() {
        cleanupUi()

        val login = views.loginField.text.toString().trim()

        
        var error = 0
        if (login.isEmpty()) {
            views.loginFieldTil.error = getString(R.string.error_empty_field_choose_user_name)
            error++
        }

        if (error == 0) {
            loginViewModel.handle(LoginAction2.SetUserName(login))
        }
    }

    private fun cleanupUi() {
        views.loginSubmit.hideKeyboard()
        views.loginFieldTil.error = null
    }

    private fun setupUi(state: LoginViewState2) {
        views.loginSubtitle.text = getString(R.string.login_signup_to, state.homeServerUrlFromUser.toReducedUrl())

        if (state.loginMode is LoginMode.SsoAndPassword) {
            views.loginSocialLoginContainer.isVisible = true
            views.loginSocialLoginButtons.ssoIdentityProviders = state.loginMode.ssoIdentityProviders?.sorted()
            views.loginSocialLoginButtons.listener = object : SocialLoginButtonsView.InteractionListener {
                override fun onProviderSelected(id: String?) {
                    loginViewModel.getSsoUrl(
                            redirectUrl = SSORedirectRouterActivity.VECTOR_REDIRECT_URL,
                            deviceId = state.deviceId,
                            providerId = id
                    )
                            ?.let { openInCustomTab(it) }
                }
            }
        } else {
            views.loginSocialLoginContainer.isVisible = false
            views.loginSocialLoginButtons.ssoIdentityProviders = null
        }
    }

    private fun setupSubmitButton() {
        views.loginSubmit.setOnClickListener { submit() }
        views.loginField.textChanges()
                .map { it.trim() }
                .onEach { text ->
                    val isNotEmpty = text.isNotEmpty()
                    views.loginFieldTil.error = null
                    views.loginSubmit.isEnabled = isNotEmpty
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun resetViewModel() {
        
    }

    override fun onError(throwable: Throwable) {
        views.loginFieldTil.error = errorFormatter.toHumanReadable(throwable)
    }

    @SuppressLint("SetTextI18n")
    override fun updateWithState(state: LoginViewState2) {
        setupUi(state)
    }
}
