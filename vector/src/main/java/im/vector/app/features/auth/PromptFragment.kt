

package im.vector.app.features.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.FragmentReauthConfirmBinding
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes

class PromptFragment : VectorBaseFragment<FragmentReauthConfirmBinding>() {

    private val viewModel: ReAuthViewModel by activityViewModel()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
            FragmentReauthConfirmBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.reAuthConfirmButton.debouncedClicks {
            onButtonClicked()
        }
    }

    private fun onButtonClicked() = withState(viewModel) { state ->
        when (state.flowType) {
            LoginFlowTypes.SSO      -> {
                viewModel.handle(ReAuthActions.StartSSOFallback)
            }
            LoginFlowTypes.PASSWORD -> {
                val password = views.passwordField.text.toString()
                if (password.isBlank()) {
                    
                    views.passwordFieldTil.error = getString(R.string.error_empty_field_your_password)
                } else {
                    views.passwordFieldTil.error = null
                    viewModel.handle(ReAuthActions.ReAuthWithPass(password))
                }
            }
            else                    -> {
                
            }
        }
    }

    override fun invalidate() = withState(viewModel) {
        when (it.flowType) {
            LoginFlowTypes.SSO      -> {
                views.passwordFieldTil.isVisible = false
                views.reAuthConfirmButton.text = getString(R.string.auth_login_sso)
            }
            LoginFlowTypes.PASSWORD -> {
                views.passwordFieldTil.isVisible = true
                views.reAuthConfirmButton.text = getString(R.string._continue)
            }
            else                    -> {
                
            }
        }

        if (it.lastErrorCode != null) {
            when (it.flowType) {
                LoginFlowTypes.SSO      -> {
                    views.genericErrorText.isVisible = true
                    views.genericErrorText.text = getString(R.string.authentication_error)
                }
                LoginFlowTypes.PASSWORD -> {
                    views.passwordFieldTil.error = getString(R.string.authentication_error)
                }
                else                    -> {
                    
                }
            }
        } else {
            views.passwordFieldTil.error = null
            views.genericErrorText.isVisible = false
        }
    }
}
