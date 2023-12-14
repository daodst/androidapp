

package im.vector.app.features.login2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.args
import im.vector.app.R
import im.vector.app.databinding.FragmentLoginWaitForEmail2Binding
import im.vector.app.features.login.LoginWaitForEmailFragmentArgument
import org.matrix.android.sdk.api.failure.is401
import javax.inject.Inject


class LoginWaitForEmailFragment2 @Inject constructor() : AbstractLoginFragment2<FragmentLoginWaitForEmail2Binding>() {

    private val params: LoginWaitForEmailFragmentArgument by args()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginWaitForEmail2Binding {
        return FragmentLoginWaitForEmail2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
    }

    override fun onResume() {
        super.onResume()

        loginViewModel.handle(LoginAction2.CheckIfEmailHasBeenValidated(0))
    }

    override fun onPause() {
        super.onPause()

        loginViewModel.handle(LoginAction2.StopEmailValidationCheck)
    }

    private fun setupUi() {
        views.loginWaitForEmailNotice.text = getString(R.string.login_wait_for_email_notice_2, params.email)
    }

    override fun onError(throwable: Throwable) {
        if (throwable.is401()) {
            
            loginViewModel.handle(LoginAction2.CheckIfEmailHasBeenValidated(10_000))
        } else {
            super.onError(throwable)
        }
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction2.ResetSignup)
    }
}
