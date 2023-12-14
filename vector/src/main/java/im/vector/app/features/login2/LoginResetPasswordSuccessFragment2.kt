

package im.vector.app.features.login2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.vector.app.databinding.FragmentLoginResetPasswordSuccess2Binding
import javax.inject.Inject


class LoginResetPasswordSuccessFragment2 @Inject constructor() : AbstractLoginFragment2<FragmentLoginResetPasswordSuccess2Binding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginResetPasswordSuccess2Binding {
        return FragmentLoginResetPasswordSuccess2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.resetPasswordSuccessSubmit.setOnClickListener { submit() }
    }

    private fun submit() {
        loginViewModel.handle(LoginAction2.PostViewEvent(LoginViewEvents2.OnResetPasswordMailConfirmationSuccessDone))
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction2.ResetResetPassword)
    }
}
