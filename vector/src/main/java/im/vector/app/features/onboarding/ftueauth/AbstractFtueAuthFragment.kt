

package im.vector.app.features.onboarding.ftueauth

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.transition.TransitionInflater
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.dialogs.UnrecognizedCertificateDialog
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.features.onboarding.OnboardingAction
import im.vector.app.features.onboarding.OnboardingViewEvents
import im.vector.app.features.onboarding.OnboardingViewModel
import im.vector.app.features.onboarding.OnboardingViewState
import kotlinx.coroutines.CancellationException
import org.matrix.android.sdk.api.failure.Failure


abstract class AbstractFtueAuthFragment<VB : ViewBinding> : VectorBaseFragment<VB>(), OnBackPressed {

    protected val viewModel: OnboardingViewModel by activityViewModel()

    private var isResetPasswordStarted = false

    
    private var displayCancelDialog = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let {
            sharedElementEnterTransition = TransitionInflater.from(it).inflateTransition(android.R.transition.move)
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeViewEvents {
            handleOnboardingViewEvents(it)
        }
    }

    private fun handleOnboardingViewEvents(viewEvents: OnboardingViewEvents) {
        when (viewEvents) {
            is OnboardingViewEvents.Failure -> showFailure(viewEvents.throwable)
            else                            ->
                
                Unit
        }
    }

    override fun showFailure(throwable: Throwable) {
        
        if (!isResumed) {
            return
        }

        when (throwable) {
            is CancellationException                  ->
                
                Unit
            is Failure.UnrecognizedCertificateFailure -> showUnrecognizedCertificateFailure(throwable)
            else                                      -> onError(throwable)
        }
    }

    private fun showUnrecognizedCertificateFailure(failure: Failure.UnrecognizedCertificateFailure) {
        
        unrecognizedCertificateDialog.show(requireActivity(),
                failure.fingerprint,
                failure.url,
                object : UnrecognizedCertificateDialog.Callback {
                    override fun onAccept() {
                        
                        viewModel.handle(OnboardingAction.UserAcceptCertificate(failure.fingerprint))
                    }

                    override fun onIgnore() {
                        
                    }

                    override fun onReject() {
                        
                    }
                })
    }

    open fun onError(throwable: Throwable) {
        super.showFailure(throwable)
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        return when {
            displayCancelDialog && viewModel.isRegistrationStarted -> {
                
                MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.login_signup_cancel_confirmation_title)
                        .setMessage(R.string.login_signup_cancel_confirmation_content)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            displayCancelDialog = false
                            vectorBaseActivity.onBackPressed()
                        }
                        .setNegativeButton(R.string.no, null)
                        .show()

                true
            }
            displayCancelDialog && isResetPasswordStarted          -> {
                
                MaterialAlertDialogBuilder(requireActivity())
                        .setTitle(R.string.login_reset_password_cancel_confirmation_title)
                        .setMessage(R.string.login_reset_password_cancel_confirmation_content)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            displayCancelDialog = false
                            vectorBaseActivity.onBackPressed()
                        }
                        .setNegativeButton(R.string.no, null)
                        .show()

                true
            }
            else                                                   -> {
                resetViewModel()
                
                false
            }
        }
    }

    final override fun invalidate() = withState(viewModel) { state ->
        
        isResetPasswordStarted = state.resetPasswordEmail.isNullOrBlank().not()

        updateWithState(state)
    }

    open fun updateWithState(state: OnboardingViewState) {
        
    }

    
    abstract fun resetViewModel()
}
