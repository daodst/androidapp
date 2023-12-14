

package im.vector.app.features.login

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
import kotlinx.coroutines.CancellationException
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import javax.net.ssl.HttpsURLConnection


abstract class AbstractLoginFragment<VB : ViewBinding> : VectorBaseFragment<VB>(), OnBackPressed {

    protected val loginViewModel: LoginViewModel by activityViewModel()

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

        loginViewModel.observeViewEvents {
            handleLoginViewEvents(it)
        }
    }

    private fun handleLoginViewEvents(loginViewEvents: LoginViewEvents) {
        when (loginViewEvents) {
            is LoginViewEvents.Failure -> showFailure(loginViewEvents.throwable)
            else                       ->
                
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
            is Failure.ServerError                    ->
                if (throwable.error.code == MatrixError.M_FORBIDDEN &&
                        throwable.httpCode == HttpsURLConnection.HTTP_FORBIDDEN ) {
                    MaterialAlertDialogBuilder(requireActivity())
                            .setTitle(R.string.dialog_title_error)
                            .setMessage(getString(R.string.login_registration_disabled))
                            .setPositiveButton(R.string.ok, null)
                            .show()
                } else {
                    onError(throwable)
                }
            is Failure.UnrecognizedCertificateFailure ->
                showUnrecognizedCertificateFailure(throwable)
            else                                      ->
                onError(throwable)
        }
    }

    private fun showUnrecognizedCertificateFailure(failure: Failure.UnrecognizedCertificateFailure) {
        
        unrecognizedCertificateDialog.show(requireActivity(),
                failure.fingerprint,
                failure.url,
                object : UnrecognizedCertificateDialog.Callback {
                    override fun onAccept() {
                        
                        loginViewModel.handle(LoginAction.UserAcceptCertificate(failure.fingerprint))
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
            displayCancelDialog && loginViewModel.isRegistrationStarted -> {
                
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
            displayCancelDialog && isResetPasswordStarted               -> {
                
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
            else                                                        -> {
                resetViewModel()
                
                false
            }
        }
    }

    final override fun invalidate() = withState(loginViewModel) { state ->
        
        isResetPasswordStarted = state.resetPasswordEmail.isNullOrBlank().not()

        updateWithState(state)
    }

    open fun updateWithState(state: LoginViewState) {
        
    }

    
    abstract fun resetViewModel()
}
