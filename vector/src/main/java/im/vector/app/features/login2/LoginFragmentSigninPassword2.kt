

package im.vector.app.features.login2

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.autofill.HintConstants
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.hidePassword
import im.vector.app.databinding.FragmentLoginSigninPassword2Binding
import im.vector.app.features.home.AvatarRenderer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.matrix.android.sdk.api.auth.login.LoginProfileInfo
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.isInvalidPassword
import reactivecircus.flowbinding.android.widget.textChanges
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection


class LoginFragmentSigninPassword2 @Inject constructor(
        private val avatarRenderer: AvatarRenderer
) : AbstractSSOLoginFragment2<FragmentLoginSigninPassword2Binding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginSigninPassword2Binding {
        return FragmentLoginSigninPassword2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSubmitButton()
        setupForgottenPasswordButton()
        setupAutoFill()

        views.passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun setupForgottenPasswordButton() {
        views.forgetPasswordButton.setOnClickListener { forgetPasswordClicked() }
    }

    private fun setupAutoFill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            views.passwordField.setAutofillHints(HintConstants.AUTOFILL_HINT_PASSWORD)
        }
    }

    private fun submit() {
        cleanupUi()

        val password = views.passwordField.text.toString()

        
        var error = 0
        if (password.isEmpty()) {
            views.passwordFieldTil.error = getString(R.string.error_empty_field_your_password)
            error++
        }

        if (error == 0) {
            loginViewModel.handle(LoginAction2.SetUserPassword(password))
        }
    }

    private fun cleanupUi() {
        views.loginSubmit.hideKeyboard()
        views.passwordFieldTil.error = null
    }

    private fun setupUi(state: LoginViewState2) {
        
        views.loginWelcomeBack.text = getString(
                R.string.login_welcome_back,
                state.loginProfileInfo()?.displayName?.takeIf { it.isNotBlank() } ?: state.userIdentifier()
        )

        avatarRenderer.render(
                profileInfo = state.loginProfileInfo() ?: LoginProfileInfo(state.userIdentifier(), null, null),
                imageView = views.loginUserIcon
        )

        views.loginWelcomeBackWarning.isVisible = ((state.loginProfileInfo as? Fail)
                ?.error as? Failure.ServerError)
                ?.httpCode == HttpsURLConnection.HTTP_NOT_FOUND 
    }

    private fun setupSubmitButton() {
        views.loginSubmit.setOnClickListener { submit() }
        views.passwordField
                .textChanges()
                .map { it.isNotEmpty() }
                .onEach {
                    views.passwordFieldTil.error = null
                    views.loginSubmit.isEnabled = it
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun forgetPasswordClicked() {
        loginViewModel.handle(LoginAction2.PostViewEvent(LoginViewEvents2.OpenResetPasswordScreen))
    }

    override fun resetViewModel() {
        
    }

    override fun onError(throwable: Throwable) {
        if (throwable.isInvalidPassword() && spaceInPassword()) {
            views.passwordFieldTil.error = getString(R.string.auth_invalid_login_param_space_in_password)
        } else {
            views.passwordFieldTil.error = errorFormatter.toHumanReadable(throwable)
        }
    }

    override fun updateWithState(state: LoginViewState2) {
        setupUi(state)

        if (state.isLoading) {
            
            views.passwordField.hidePassword()
        }
    }

    
    private fun spaceInPassword() = views.passwordField.text.toString().let { it.trim() != it }
}
