

package im.vector.app.features.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.vector.app.R
import im.vector.app.core.utils.openUrlInChromeCustomTab
import im.vector.app.databinding.FragmentLoginServerSelectionBinding
import me.gujun.android.span.span
import javax.inject.Inject


class LoginServerSelectionFragment @Inject constructor() : AbstractLoginFragment<FragmentLoginServerSelectionBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginServerSelectionBinding {
        return FragmentLoginServerSelectionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initTextViews()
    }

    private fun initViews() {
        views.loginServerChoiceEmsLearnMore.debouncedClicks { learnMore() }
        views.loginServerChoiceMatrixOrg.debouncedClicks { selectMatrixOrg() }
        views.loginServerChoiceEms.debouncedClicks { selectEMS() }
        views.loginServerChoiceOther.debouncedClicks { selectOther() }
        views.loginServerIKnowMyIdSubmit.debouncedClicks { loginWithMatrixId() }
    }

    private fun updateSelectedChoice(state: LoginViewState) {
        views.loginServerChoiceMatrixOrg.isChecked = state.serverType == ServerType.MatrixOrg
    }

    private fun initTextViews() {
        views.loginServerChoiceEmsLearnMore.text = span {
            text = getString(R.string.login_server_modular_learn_more)
            textDecorationLine = "underline"
        }
    }

    private fun learnMore() {
        openUrlInChromeCustomTab(requireActivity(), null, EMS_LINK)
    }

    private fun selectMatrixOrg() {
        loginViewModel.handle(LoginAction.UpdateServerType(ServerType.MatrixOrg))
    }

    private fun selectEMS() {
        loginViewModel.handle(LoginAction.UpdateServerType(ServerType.EMS))
    }

    private fun selectOther() {
        loginViewModel.handle(LoginAction.UpdateServerType(ServerType.Other))
    }

    private fun loginWithMatrixId() {
        loginViewModel.handle(LoginAction.UpdateSignMode(SignMode.SignInWithMatrixId))
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction.ResetHomeServerType)
    }

    override fun updateWithState(state: LoginViewState) {
        updateSelectedChoice(state)
    }
}
