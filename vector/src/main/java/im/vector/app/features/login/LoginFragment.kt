

package im.vector.app.features.login

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.autofill.HintConstants
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.hidePassword
import im.vector.app.core.extensions.toReducedUrl
import im.vector.app.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.failure.isInvalidPassword
import reactivecircus.flowbinding.android.widget.textChanges
import javax.inject.Inject


class LoginFragment @Inject constructor() : AbstractSSOLoginFragment<FragmentLoginBinding>() {

    private var isSignupMode = false

    
    
    private var isNumericOnlyUserIdForbidden = false

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSubmitButton()
        setupForgottenPasswordButton()

        views.passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun setupForgottenPasswordButton() {
        views.forgetPasswordButton.debouncedClicks { forgetPasswordClicked() }
    }

    private fun setupAutoFill(state: LoginViewState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when (state.signMode) {
                SignMode.Unknown            -> error("developer error")
                SignMode.SignUp             -> {
                    views.loginField.setAutofillHints(HintConstants.AUTOFILL_HINT_NEW_USERNAME)
                    views.passwordField.setAutofillHints(HintConstants.AUTOFILL_HINT_NEW_PASSWORD)
                }
                SignMode.SignIn,
                SignMode.SignInWithMatrixId -> {
                    views.loginField.setAutofillHints(HintConstants.AUTOFILL_HINT_USERNAME)
                    views.passwordField.setAutofillHints(HintConstants.AUTOFILL_HINT_PASSWORD)
                }
            }
        }
    }

    private fun setupSocialLoginButtons(state: LoginViewState) {
        views.loginSocialLoginButtons.mode = when (state.signMode) {
            SignMode.Unknown            -> error("developer error")
            SignMode.SignUp             -> SocialLoginButtonsView.Mode.MODE_SIGN_UP
            SignMode.SignIn,
            SignMode.SignInWithMatrixId -> SocialLoginButtonsView.Mode.MODE_SIGN_IN
        }
    }

    private fun submit() {
        cleanupUi()

        val login = views.loginField.text.toString()
        val password = views.passwordField.text.toString()

        
        var error = 0
        if (login.isEmpty()) {
            views.loginFieldTil.error = getString(if (isSignupMode) {
                R.string.error_empty_field_choose_user_name
            } else {
                R.string.error_empty_field_enter_user_name
            })
            error++
        }
        if (isSignupMode && isNumericOnlyUserIdForbidden && login.isDigitsOnly()) {
            views.loginFieldTil.error = getString(R.string.error_forbidden_digits_only_username)
            error++
        }
        if (password.isEmpty()) {
            views.passwordFieldTil.error = getString(if (isSignupMode) {
                R.string.error_empty_field_choose_password
            } else {
                R.string.error_empty_field_your_password
            })
            error++
        }

        if (error == 0) {
            loginViewModel.handle(LoginAction.LoginOrRegister(login, password, getString(R.string.login_default_session_public_name)))
        }
    }

    private fun cleanupUi() {
        views.loginSubmit.hideKeyboard()
        views.loginFieldTil.error = null
        views.passwordFieldTil.error = null
    }

    private fun setupUi(state: LoginViewState) {
        views.loginFieldTil.hint = getString(when (state.signMode) {
            SignMode.Unknown            -> error("developer error")
            SignMode.SignUp             -> R.string.login_signup_username_hint
            SignMode.SignIn             -> R.string.login_signin_username_hint
            SignMode.SignInWithMatrixId -> R.string.login_signin_matrix_id_hint
        })

        
        if (state.signMode == SignMode.SignInWithMatrixId) {
            views.loginServerIcon.isVisible = false
            views.loginTitle.text = getString(R.string.login_signin_matrix_id_title)
            views.loginNotice.text = getString(R.string.login_signin_matrix_id_notice)
            views.loginPasswordNotice.isVisible = true
        } else {
            val resId = when (state.signMode) {
                SignMode.Unknown            -> error("developer error")
                SignMode.SignUp             -> R.string.login_signup_to
                SignMode.SignIn             -> R.string.login_connect_to
                SignMode.SignInWithMatrixId -> R.string.login_connect_to
            }

            when (state.serverType) {
                ServerType.MatrixOrg -> {
                    views.loginServerIcon.isVisible = true
                    views.loginServerIcon.setImageResource(R.drawable.ic_logo_matrix_org)
                    views.loginTitle.text = getString(resId, state.homeServerUrlFromUser.toReducedUrl())
                    views.loginNotice.text = getString(R.string.login_server_matrix_org_text)
                }
                ServerType.EMS       -> {
                    views.loginServerIcon.isVisible = true
                    views.loginServerIcon.setImageResource(R.drawable.ic_logo_element_matrix_services)
                    views.loginTitle.text = getString(resId, "Element Matrix Services")
                    views.loginNotice.text = getString(R.string.login_server_modular_text)
                }
                ServerType.Other     -> {
                    views.loginServerIcon.isVisible = false
                    views.loginTitle.text = getString(resId, state.homeServerUrlFromUser.toReducedUrl())
                    views.loginNotice.text = getString(R.string.login_server_other_text)
                }
                ServerType.Unknown   -> Unit 
            }
            views.loginPasswordNotice.isVisible = false

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
    }

    private fun setupButtons(state: LoginViewState) {
        views.forgetPasswordButton.isVisible = state.signMode == SignMode.SignIn

        views.loginSubmit.text = getString(when (state.signMode) {
            SignMode.Unknown            -> error("developer error")
            SignMode.SignUp             -> R.string.login_signup_submit
            SignMode.SignIn,
            SignMode.SignInWithMatrixId -> R.string.login_signin
        })
    }

    private fun setupSubmitButton() {
        views.loginSubmit.debouncedClicks { submit() }
        combine(
                views.loginField.textChanges().map { it.trim().isNotEmpty() },
                views.passwordField.textChanges().map { it.isNotEmpty() }
        ) { isLoginNotEmpty, isPasswordNotEmpty ->
            isLoginNotEmpty && isPasswordNotEmpty
        }
                .onEach {
                    views.loginFieldTil.error = null
                    views.passwordFieldTil.error = null
                    views.loginSubmit.isEnabled = it
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun forgetPasswordClicked() {
        loginViewModel.handle(LoginAction.PostViewEvent(LoginViewEvents.OnForgetPasswordClicked))
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction.ResetLogin)
    }

    override fun onError(throwable: Throwable) {
        
        if (throwable is Failure.ServerError &&
                throwable.error.code == MatrixError.M_WEAK_PASSWORD) {
            views.passwordFieldTil.error = errorFormatter.toHumanReadable(throwable)
        } else {
            views.loginFieldTil.error = errorFormatter.toHumanReadable(throwable)
        }
    }

    override fun updateWithState(state: LoginViewState) {
        isSignupMode = state.signMode == SignMode.SignUp
        isNumericOnlyUserIdForbidden = state.serverType == ServerType.MatrixOrg

        setupUi(state)
        setupAutoFill(state)
        setupSocialLoginButtons(state)
        setupButtons(state)

        when (state.asyncLoginAction) {
            is Loading -> {
                
                views.passwordField.hidePassword()
            }
            is Fail    -> {
                val error = state.asyncLoginAction.error
                if (error is Failure.ServerError &&
                        error.error.code == MatrixError.M_FORBIDDEN &&
                        error.error.message.isEmpty()) {
                    
                    views.loginFieldTil.error = getString(R.string.login_login_with_email_error)
                } else {
                    
                    views.loginFieldTil.error = " "
                    if (error.isInvalidPassword() && spaceInPassword()) {
                        views.passwordFieldTil.error = getString(R.string.auth_invalid_login_param_space_in_password)
                    } else {
                        views.passwordFieldTil.error = errorFormatter.toHumanReadable(error)
                    }
                }
            }
            
            else       -> Unit
        }

        when (state.asyncRegistration) {
            is Loading -> {
                
                views.passwordField.hidePassword()
            }
            
            else       -> Unit
        }
    }

    
    private fun spaceInPassword() = views.passwordField.text.toString().let { it.trim() != it }
}