

package im.vector.app.features.onboarding.ftueauth

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.args
import im.vector.app.R
import im.vector.app.databinding.FragmentLoginWaitForEmailBinding
import im.vector.app.features.onboarding.OnboardingAction
import im.vector.app.features.onboarding.RegisterAction
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.failure.is401
import javax.inject.Inject

@Parcelize
data class FtueAuthWaitForEmailFragmentArgument(
        val email: String
) : Parcelable


class FtueAuthWaitForEmailFragment @Inject constructor() : AbstractFtueAuthFragment<FragmentLoginWaitForEmailBinding>() {

    private val params: FtueAuthWaitForEmailFragmentArgument by args()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginWaitForEmailBinding {
        return FragmentLoginWaitForEmailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    override fun onResume() {
        super.onResume()

        viewModel.handle(OnboardingAction.PostRegisterAction(RegisterAction.CheckIfEmailHasBeenValidated(0)))
    }

    override fun onPause() {
        super.onPause()

        viewModel.handle(OnboardingAction.StopEmailValidationCheck)
    }

    private fun setupUi() {
        views.loginWaitForEmailNotice.text = getString(R.string.login_wait_for_email_notice, params.email)
    }

    override fun onError(throwable: Throwable) {
        if (throwable.is401()) {
            
            viewModel.handle(OnboardingAction.PostRegisterAction(RegisterAction.CheckIfEmailHasBeenValidated(10_000)))
        } else {
            super.onError(throwable)
        }
    }

    override fun resetViewModel() {
        viewModel.handle(OnboardingAction.ResetAuthenticationAttempt)
    }
}
