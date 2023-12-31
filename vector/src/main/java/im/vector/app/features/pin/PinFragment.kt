

package im.vector.app.features.pin

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.args
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.utils.toast
import im.vector.app.databinding.FragmentPinBinding
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs
import im.vector.app.features.settings.VectorPreferences
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class PinArgs(
        val pinMode: PinMode
) : Parcelable

class PinFragment @Inject constructor(
        private val pinCodeStore: PinCodeStore,
        private val vectorPreferences: VectorPreferences
) : VectorBaseFragment<FragmentPinBinding>() {

    private val fragmentArgs: PinArgs by args()

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPinBinding {
        return FragmentPinBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (fragmentArgs.pinMode) {
            PinMode.CREATE -> showCreateFragment()
            PinMode.AUTH   -> showAuthFragment()
            PinMode.MODIFY -> showCreateFragment() 
        }
    }

    private fun showCreateFragment() {
        val createFragment = PFLockScreenFragment()
        val builder = PFFLockScreenConfiguration.Builder(requireContext())
                .setNewCodeValidation(true)
                .setTitle(getString(R.string.create_pin_title))
                .setNewCodeValidationTitle(getString(R.string.create_pin_confirm_title))
                .setMode(PFFLockScreenConfiguration.MODE_CREATE)

        createFragment.setConfiguration(builder.build())
        createFragment.setCodeCreateListener(object : PFLockScreenFragment.OnPFLockScreenCodeCreateListener {
            override fun onNewCodeValidationFailed() {
                Toast.makeText(requireContext(), getString(R.string.create_pin_confirm_failure), Toast.LENGTH_SHORT).show()
            }

            override fun onPinCodeEnteredFirst(pinCode: String?): Boolean {
                return false
            }

            override fun onCodeCreated(encodedCode: String) {
                lifecycleScope.launch {
                    pinCodeStore.storeEncodedPin(encodedCode)
                    vectorBaseActivity.setResult(Activity.RESULT_OK)
                    vectorBaseActivity.finish()
                }
            }
        })
        replaceFragment(R.id.pinFragmentContainer, createFragment)
    }

    private fun showAuthFragment() {
        val encodedPin = pinCodeStore.getEncodedPin() ?: return
        val authFragment = PFLockScreenFragment()
        val canUseBiometrics = pinCodeStore.getRemainingBiometricsAttemptsNumber() > 0
        val builder = PFFLockScreenConfiguration.Builder(requireContext())
                .setAutoShowBiometric(true)
                .setUseBiometric(vectorPreferences.useBiometricsToUnlock() && canUseBiometrics)
                .setAutoShowBiometric(canUseBiometrics)
                .setTitle(getString(R.string.auth_pin_title))
                .setLeftButton(getString(R.string.auth_pin_forgot))
                .setClearCodeOnError(true)
                .setMode(PFFLockScreenConfiguration.MODE_AUTH)
        authFragment.setConfiguration(builder.build())
        authFragment.setEncodedPinCode(encodedPin)
        authFragment.setOnLeftButtonClickListener {
            displayForgotPinWarningDialog()
        }
        authFragment.setLoginListener(object : PFLockScreenFragment.OnPFLockScreenLoginListener {
            override fun onPinLoginFailed() {
                onWrongPin()
            }

            override fun onBiometricAuthSuccessful() {
                pinCodeStore.resetCounters()
                vectorBaseActivity.setResult(Activity.RESULT_OK)
                vectorBaseActivity.finish()
            }

            override fun onBiometricAuthLoginFailed() {
                val remainingAttempts = pinCodeStore.onWrongBiometrics()
                if (remainingAttempts <= 0) {
                    
                    builder.setUseBiometric(false)
                    authFragment.setConfiguration(builder.build())
                }
            }

            override fun onCodeInputSuccessful() {
                pinCodeStore.resetCounters()
                vectorBaseActivity.setResult(Activity.RESULT_OK)
                vectorBaseActivity.finish()
            }
        })
        replaceFragment(R.id.pinFragmentContainer, authFragment)
    }

    private fun onWrongPin() {
        val remainingAttempts = pinCodeStore.onWrongPin()
        when {
            remainingAttempts > 1  ->
                requireActivity().toast(resources.getQuantityString(R.plurals.wrong_pin_message_remaining_attempts, remainingAttempts, remainingAttempts))
            remainingAttempts == 1 ->
                requireActivity().toast(R.string.wrong_pin_message_last_remaining_attempt)
            else                   -> {
                requireActivity().toast(R.string.too_many_pin_failures)
                
                MainActivity.restartApp(requireActivity(), MainActivityArgs(clearCredentials = true))
            }
        }
    }

    private fun displayForgotPinWarningDialog() {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.auth_pin_reset_title))
                .setMessage(getString(R.string.auth_pin_reset_content))
                .setPositiveButton(getString(R.string.auth_pin_new_pin_action)) { _, _ ->
                    launchResetPinFlow()
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()
    }

    private fun launchResetPinFlow() {
        MainActivity.restartApp(requireActivity(), MainActivityArgs(clearCredentials = true))
    }
}
