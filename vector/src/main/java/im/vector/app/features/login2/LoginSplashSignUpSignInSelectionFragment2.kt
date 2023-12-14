

package im.vector.app.features.login2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import im.vector.app.BuildConfig
import im.vector.app.databinding.FragmentLoginSplash2Binding
import im.vector.app.features.settings.VectorPreferences
import javax.inject.Inject


class LoginSplashSignUpSignInSelectionFragment2 @Inject constructor(
        private val vectorPreferences: VectorPreferences
) : AbstractLoginFragment2<FragmentLoginSplash2Binding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginSplash2Binding {
        return FragmentLoginSplash2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        views.loginSignupSigninSignUp.setOnClickListener { signUp() }
        views.loginSignupSigninSignIn.setOnClickListener { signIn() }

        if (BuildConfig.DEBUG || vectorPreferences.developerMode()) {
            views.loginSplashVersion.isVisible = true
            @SuppressLint("SetTextI18n")
            views.loginSplashVersion.text = "Version : ${BuildConfig.VERSION_NAME}\n" +
                    "Branch: ${BuildConfig.GIT_BRANCH_NAME}\n" +
                    "Build: ${BuildConfig.BUILD_NUMBER}"
            views.loginSplashVersion.debouncedClicks { navigator.openDebug(requireContext()) }
        }
    }

    private fun signUp() {
        loginViewModel.handle(LoginAction2.UpdateSignMode(SignMode2.SignUp))
    }

    private fun signIn() {
        loginViewModel.handle(LoginAction2.UpdateSignMode(SignMode2.SignIn))
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction2.ResetSignMode)
    }
}
