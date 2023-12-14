

package im.vector.app.features.login2

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.autofill.HintConstants
import androidx.lifecycle.lifecycleScope
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.databinding.FragmentLoginSigninUsername2Binding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import reactivecircus.flowbinding.android.widget.textChanges
import javax.inject.Inject


class LoginFragmentSigninUsername2 @Inject constructor() : AbstractLoginFragment2<FragmentLoginSigninUsername2Binding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginSigninUsername2Binding {
        return FragmentLoginSigninUsername2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSubmitButton()
        setupAutoFill()
        views.loginChooseAServer.setOnClickListener {
            loginViewModel.handle(LoginAction2.ChooseAServerForSignin)
        }
    }

    private fun setupAutoFill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            views.loginField.setAutofillHints(HintConstants.AUTOFILL_HINT_USERNAME)
        }
    }

    private fun submit() {
        cleanupUi()

        val login = views.loginField.text.toString()

        
        var error = 0
        if (login.isEmpty()) {
            views.loginFieldTil.error = getString(R.string.error_empty_field_enter_user_name)
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

    private fun setupSubmitButton() {
        views.loginSubmit.setOnClickListener { submit() }
        views.loginField.textChanges()
                .map { it.trim().isNotEmpty() }
                .onEach {
                    views.loginFieldTil.error = null
                    views.loginSubmit.isEnabled = it
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction2.ResetSignin)
    }

    override fun onError(throwable: Throwable) {
        if (throwable is Failure.ServerError &&
                throwable.error.code == MatrixError.M_FORBIDDEN &&
                throwable.error.message.isEmpty()) {
            
            views.loginFieldTil.error = getString(R.string.login_login_with_email_error)
        } else {
            views.loginFieldTil.error = errorFormatter.toHumanReadable(throwable)
        }
    }
}
