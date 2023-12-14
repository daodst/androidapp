

package im.vector.app.features.onboarding.ftueauth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.tencent.mmkv.MMKV
import im.vector.app.R
import im.vector.app.WalletHandler
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.extensions.registerStartForActivityResult
import im.vector.app.core.utils.ensureProtocol
import im.vector.app.core.utils.openUrlInChromeCustomTab
import im.vector.app.databinding.FragmentLoginServerUrlFormBinding
import im.vector.app.features.home.HomeActivity
import im.vector.app.features.login.EMS_LINK
import im.vector.app.features.login.LoginConfig
import im.vector.app.features.login.ServerType
import im.vector.app.features.onboarding.OnboardingAction
import im.vector.app.features.onboarding.OnboardingActivity
import im.vector.app.features.onboarding.OnboardingFlow
import im.vector.app.features.onboarding.OnboardingViewState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.widget.textChanges
import timber.log.Timber
import javax.inject.Inject


class FtueAuthServerUrlFormFragment @Inject constructor() : AbstractFtueAuthFragment<FragmentLoginServerUrlFormBinding>() {

    val KEY_IM_URL = "synImUrl";

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginServerUrlFormBinding {
        return FragmentLoginServerUrlFormBinding.inflate(inflater, container, false)
    }

    var loginConfig: LoginConfig? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginConfig = vectorBaseActivity.intent.getParcelableExtra<LoginConfig?>(OnboardingActivity.EXTRA_CONFIG)

        setupViews()
        setupHomeServerField()
    }

    private fun setupViews() {
        views.loginServerUrlFormLearnMore.setOnClickListener { learnMore() }
        views.loginServerUrlFormClearHistory.setOnClickListener { clearHistory() }
        views.loginServerUrlFormSubmit.setOnClickListener { submit() }
        views.splashLoginContainer.isVisible = false
        views.splashLogin.isVisible = true
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

        var imUrl = MMKV.defaultMMKV()?.decodeString(KEY_IM_URL)
        Timber.i("imUrl=$imUrl")
        imUrl?.let {
            if (!it.isEmpty()) {
                
                views.loginServerUrlFormHomeServerUrl.setText(it);
                walletLoginResult.launch(WalletHandler.getWalletLoginIntent(requireContext()))
            }
        }
    }

    private fun setupUi(state: OnboardingViewState) {
        when (state.serverType) {
            ServerType.EMS -> {
                views.loginServerUrlFormIcon.isVisible = true
                views.loginServerUrlFormTitle.text = getString(R.string.login_connect_to_modular)
                views.loginServerUrlFormText.text = getString(R.string.login_server_url_form_modular_text)
                views.loginServerUrlFormLearnMore.isVisible = true
                views.loginServerUrlFormHomeServerUrlTil.hint = getText(R.string.login_server_url_form_modular_hint)
                views.loginServerUrlFormNotice.text = getString(R.string.login_server_url_form_modular_notice)
            }
            else           -> {
                views.loginServerUrlFormIcon.isVisible = false
                views.loginServerUrlFormTitle.text = getString(R.string.login_server_other_title)
                views.loginServerUrlFormText.text = getString(R.string.login_connect_to_a_custom_server)
                views.loginServerUrlFormLearnMore.isVisible = false
                views.loginServerUrlFormHomeServerUrlTil.hint = getText(R.string.login_server_url_form_other_hint)
                views.loginServerUrlFormNotice.text = getString(R.string.login_server_url_form_common_notice)
            }
        }
        var imUrl = MMKV.defaultMMKV()?.decodeString(KEY_IM_URL)
        val completions = listOf(imUrl)
        views.loginServerUrlFormHomeServerUrl.setAdapter(
                ArrayAdapter(
                        requireContext(),
                        R.layout.item_completion_homeserver,
                        completions
                )
        )
        views.loginServerUrlFormHomeServerUrlTil.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                .takeIf { completions.isNotEmpty() }
                ?: TextInputLayout.END_ICON_NONE
    }

    private fun learnMore() {
        openUrlInChromeCustomTab(requireActivity(), null, EMS_LINK)
    }

    private fun clearHistory() {
        viewModel.handle(OnboardingAction.ClearHomeServerHistory)
    }

    override fun resetViewModel() {
        viewModel.handle(OnboardingAction.ResetHomeServerUrl)
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
                
                
                walletLoginResult.launch(WalletHandler.getWalletLoginIntent(requireContext()))
            }
        }
    }

    val walletLoginResult = registerStartForActivityResult {
        if (it.resultCode == Activity.RESULT_OK) {

            val serverUrl = views.loginServerUrlFormHomeServerUrl.text.toString().trim().ensureProtocol()

            val address = it.data?.getStringExtra("address")!!
            val pwd = it.data?.getStringExtra("password")!!
            val sign = it.data?.getStringExtra("sign")!!;
            val pubKey = it.data?.getStringExtra("pubkey")!!
            val privateKey = it.data?.getStringExtra("privateKey")!!
            val timestamp = it.data?.getStringExtra("params")!!
            val chat_pub_key = it.data?.getStringExtra("chat_pub_key")!!
            val chat_sign = it.data?.getStringExtra("chatSign")!!
            Timber.i("$address, $pwd, $sign, $pubKey, $timestamp,chat_pub_key:${chat_pub_key},chatSign:${chat_sign}")
            viewModel.handle(
                    OnboardingAction.HomeServerChange.SelectHomeServerLogin(
                            serverUrl,
                            onboardingFlow = OnboardingFlow.SignIn,
                            username = address,
                            password = pwd,
                            sign = sign,
                            pubKey = pubKey,
                            timestamp = timestamp,
                            initialDeviceName = getString(R.string.login_default_session_public_name),
                            privateKey = privateKey,
                            chat_pub_key = chat_pub_key,
                            chat_sign = chat_sign,

                            )
            )
        } else if (it.resultCode == Activity.RESULT_CANCELED) {
            if (loginConfig?.isLogOut ?: false) {
                startActivity(HomeActivity.newIntent(requireContext(), clearTask = true))
            }
            activity?.finish()
        }
    }

    private fun cleanupUi() {
        views.loginServerUrlFormSubmit.hideKeyboard()
        views.loginServerUrlFormHomeServerUrlTil.error = null
    }

    override fun onError(throwable: Throwable) {

        val readable = errorFormatter.toHumanReadable(throwable)
        if ("err servername".equals(readable)) {
            MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(getString(R.string.err_servername_title))
                    .setMessage(getString(R.string.err_servername_msg))
                    .setPositiveButton(getString(R.string.err_servername_bt_tips), object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            
                            try {
                                val activityName = "com.app.node.NodeListsActivity"
                                val clazz = Class.forName(activityName)
                                val intent = Intent(activity, clazz)
                                intent.putExtra("isLogin",true)
                                startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            activity?.finish()
                        }
                    })
                    .show()
        } else {
            Toast.makeText(activity, errorFormatter.toHumanReadable(throwable), Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }

    override fun updateWithState(state: OnboardingViewState) {
        setupUi(state)

        views.loginServerUrlFormClearHistory.isInvisible = state.knownCustomHomeServersUrls.isEmpty()
    }
}
