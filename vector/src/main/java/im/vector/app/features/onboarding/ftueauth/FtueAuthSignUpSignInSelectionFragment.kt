

package im.vector.app.features.onboarding.ftueauth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.extensions.toReducedUrl
import im.vector.app.databinding.FragmentLoginSignupSigninSelectionBinding
import im.vector.app.features.login.LoginMode
import im.vector.app.features.login.SSORedirectRouterActivity
import im.vector.app.features.login.ServerType
import im.vector.app.features.login.SignMode
import im.vector.app.features.login.SocialLoginButtonsView
import im.vector.app.features.login.ssoIdentityProviders
import im.vector.app.features.onboarding.OnboardingAction
import im.vector.app.features.onboarding.OnboardingViewState
import javax.inject.Inject


class FtueAuthSignUpSignInSelectionFragment @Inject constructor() : AbstractSSOFtueAuthFragment<FragmentLoginSignupSigninSelectionBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginSignupSigninSelectionBinding {
        return FragmentLoginSignupSigninSelectionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        views.loginSignupSigninSubmit.setOnClickListener { submit() }
        views.loginSignupSigninSignIn.setOnClickListener { signIn() }
    }

    private fun render(state: OnboardingViewState) {
        when (state.serverType) {
            ServerType.MatrixOrg -> renderServerInformation(
                    icon = R.drawable.ic_logo_matrix_org,
                    title = getString(R.string.login_connect_to, state.selectedHomeserver.userFacingUrl.toReducedUrl()),
                    subtitle = getString(R.string.login_server_matrix_org_text)
            )
            ServerType.EMS       -> renderServerInformation(
                    icon = R.drawable.ic_logo_element_matrix_services,
                    title = getString(R.string.login_connect_to_modular),
                    subtitle = state.selectedHomeserver.userFacingUrl.toReducedUrl()
            )
            ServerType.Other     -> renderServerInformation(
                    icon = null,
                    title = getString(R.string.login_server_other_title),
                    subtitle = getString(R.string.login_connect_to, state.selectedHomeserver.userFacingUrl.toReducedUrl())
            )
            ServerType.Unknown   -> Unit 
        }

        when (state.selectedHomeserver.preferredLoginMode) {
            is LoginMode.SsoAndPassword -> {
                views.loginSignupSigninSignInSocialLoginContainer.isVisible = true
                views.loginSignupSigninSocialLoginButtons.ssoIdentityProviders = state.selectedHomeserver.preferredLoginMode.ssoIdentityProviders()?.sorted()
                views.loginSignupSigninSocialLoginButtons.listener = object : SocialLoginButtonsView.InteractionListener {
                    override fun onProviderSelected(id: String?) {
                        viewModel.getSsoUrl(
                                redirectUrl = SSORedirectRouterActivity.VECTOR_REDIRECT_URL,
                                deviceId = state.deviceId,
                                providerId = id
                        )
                                ?.let { openInCustomTab(it) }
                    }
                }
            }
            else                        -> {
                
                views.loginSignupSigninSignInSocialLoginContainer.isVisible = false
                views.loginSignupSigninSocialLoginButtons.ssoIdentityProviders = null
            }
        }
    }

    private fun renderServerInformation(@DrawableRes icon: Int?, title: String, subtitle: String) {
        icon?.let { views.loginSignupSigninServerIcon.setImageResource(it) }
        views.loginSignupSigninServerIcon.isVisible = icon != null
        views.loginSignupSigninServerIcon.setImageResource(R.drawable.ic_logo_matrix_org)
        views.loginSignupSigninTitle.text = title
        views.loginSignupSigninText.text = subtitle
    }

    private fun setupButtons(state: OnboardingViewState) {
        when (state.selectedHomeserver.preferredLoginMode) {
            is LoginMode.Sso -> {
                
                views.loginSignupSigninSubmit.text = getString(R.string.login_signin_sso)
                views.loginSignupSigninSignIn.isVisible = false
            }
            else             -> {
                views.loginSignupSigninSubmit.text = getString(R.string.login_signup)
                views.loginSignupSigninSignIn.isVisible = true
            }
        }
    }

    private fun submit() = withState(viewModel) { state ->
        if (state.selectedHomeserver.preferredLoginMode is LoginMode.Sso) {
            viewModel.getSsoUrl(
                    redirectUrl = SSORedirectRouterActivity.VECTOR_REDIRECT_URL,
                    deviceId = state.deviceId,
                    providerId = null
            )
                    ?.let { openInCustomTab(it) }
        } else {
            viewModel.handle(OnboardingAction.UpdateSignMode(SignMode.SignUp))
        }
    }

    private fun signIn() {
        viewModel.handle(OnboardingAction.UpdateSignMode(SignMode.SignIn))
    }

    override fun resetViewModel() {
        viewModel.handle(OnboardingAction.ResetSignMode)
    }

    override fun updateWithState(state: OnboardingViewState) {
        render(state)
        setupButtons(state)
    }
}
