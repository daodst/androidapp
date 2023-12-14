

package im.vector.app.features.onboarding.ftueauth

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.airbnb.mvrx.args
import im.vector.app.databinding.FragmentLoginCaptchaBinding
import im.vector.app.features.onboarding.OnboardingAction
import im.vector.app.features.onboarding.OnboardingViewState
import im.vector.app.features.onboarding.RegisterAction
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class FtueAuthLegacyStyleCaptchaFragmentArgument(
        val siteKey: String
) : Parcelable


class FtueAuthLegacyStyleCaptchaFragment @Inject constructor(
        private val captchaWebview: CaptchaWebview
) : AbstractFtueAuthFragment<FragmentLoginCaptchaBinding>() {

    private val params: FtueAuthLegacyStyleCaptchaFragmentArgument by args()
    private var isWebViewLoaded = false

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginCaptchaBinding {
        return FragmentLoginCaptchaBinding.inflate(inflater, container, false)
    }

    override fun resetViewModel() {
        viewModel.handle(OnboardingAction.ResetAuthenticationAttempt)
    }

    override fun updateWithState(state: OnboardingViewState) {
        if (!isWebViewLoaded) {
            captchaWebview.setupWebView(this, views.loginCaptchaWevView, views.loginCaptchaProgress, params.siteKey, state) {
                viewModel.handle(OnboardingAction.PostRegisterAction(RegisterAction.CaptchaDone(it)))
            }
            isWebViewLoaded = true
        }
    }
}
