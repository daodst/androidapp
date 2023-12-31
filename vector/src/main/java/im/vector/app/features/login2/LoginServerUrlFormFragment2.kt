

package im.vector.app.features.login2

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.utils.ensureProtocol
import im.vector.app.databinding.FragmentLoginServerUrlForm2Binding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import reactivecircus.flowbinding.android.widget.textChanges
import java.net.UnknownHostException
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection


class LoginServerUrlFormFragment2 @Inject constructor() : AbstractLoginFragment2<FragmentLoginServerUrlForm2Binding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginServerUrlForm2Binding {
        return FragmentLoginServerUrlForm2Binding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupHomeServerField()
    }

    private fun setupViews() {
        views.loginServerUrlFormClearHistory.setOnClickListener { clearHistory() }
        views.loginServerUrlFormSubmit.setOnClickListener { submit() }
    }

    private fun setupHomeServerField() {
        views.loginServerUrlFormHomeServerUrl.textChanges()
                .onEach {
                    views.loginServerUrlFormHomeServerUrlTil.error = null
                    views.loginServerUrlFormSubmit.isEnabled = it.isNotBlank()
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)

        views.loginServerUrlFormHomeServerUrl.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                views.loginServerUrlFormHomeServerUrl.dismissDropDown()
                submit()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun setupUi(state: LoginViewState2) {
        val completions = state.knownCustomHomeServersUrls + if (BuildConfig.DEBUG) listOf("http://10.0.2.2:8080") else emptyList()
        views.loginServerUrlFormHomeServerUrl.setAdapter(ArrayAdapter(
                requireContext(),
                R.layout.item_completion_homeserver,
                completions
        ))
        views.loginServerUrlFormHomeServerUrlTil.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                .takeIf { completions.isNotEmpty() }
                ?: TextInputLayout.END_ICON_NONE

        views.loginServerUrlFormClearHistory.isInvisible = state.knownCustomHomeServersUrls.isEmpty()
    }

    private fun clearHistory() {
        loginViewModel.handle(LoginAction2.ClearHomeServerHistory)
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction2.ResetHomeServerUrl)
    }

    @SuppressLint("SetTextI18n")
    private fun submit() {
        cleanupUi()

        
        val serverUrl = views.loginServerUrlFormHomeServerUrl.text.toString().trim().ensureProtocol()

        when {
            serverUrl.isBlank() -> {
                views.loginServerUrlFormHomeServerUrlTil.error = getString(R.string.login_error_invalid_home_server)
            }
            else                -> {
                views.loginServerUrlFormHomeServerUrl.setText(serverUrl, false )
                loginViewModel.handle(LoginAction2.UpdateHomeServer(serverUrl))
            }
        }
    }

    private fun cleanupUi() {
        views.loginServerUrlFormSubmit.hideKeyboard()
        views.loginServerUrlFormHomeServerUrlTil.error = null
    }

    override fun onError(throwable: Throwable) {
        views.loginServerUrlFormHomeServerUrlTil.error = if (throwable is Failure.NetworkConnection &&
                throwable.ioException is UnknownHostException) {
            
            getString(R.string.login_error_homeserver_not_found)
        } else {
            if (throwable is Failure.ServerError &&
                    throwable.error.code == MatrixError.M_FORBIDDEN &&
                    throwable.httpCode == HttpsURLConnection.HTTP_FORBIDDEN ) {
                getString(R.string.login_registration_disabled)
            } else {
                errorFormatter.toHumanReadable(throwable)
            }
        }
    }

    override fun updateWithState(state: LoginViewState2) {
        setupUi(state)
    }
}
