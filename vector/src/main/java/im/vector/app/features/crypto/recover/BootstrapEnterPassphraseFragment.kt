

package im.vector.app.features.crypto.recover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragmentHost
import im.vector.app.databinding.FragmentBootstrapEnterPassphraseBinding
import im.vector.app.features.settings.VectorLocale
import im.vector.lib.core.utils.flow.throttleFirst
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.widget.editorActionEvents
import reactivecircus.flowbinding.android.widget.textChanges
import javax.inject.Inject

class BootstrapEnterPassphraseFragment @Inject constructor() :
        VectorBaseFragmentHost<FragmentBootstrapEnterPassphraseBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentBootstrapEnterPassphraseBinding {
        return FragmentBootstrapEnterPassphraseBinding.inflate(inflater, container, false)
    }

    val sharedViewModel: BootstrapSharedViewModel by parentFragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        views.bootstrapDescriptionText.text = getString(R.string.set_a_security_phrase_notice)
        views.ssssPassphraseEnterEdittext.hint = getString(R.string.set_a_security_phrase_hint)

        withState(sharedViewModel) {
            
            views.ssssPassphraseEnterEdittext.setText(it.passphraseSelf ?: "")
        }
        views.ssssPassphraseEnterEdittext.editorActionEvents()
                .throttleFirst(300)
                .onEach {
                    if (it.actionId == EditorInfo.IME_ACTION_DONE) {
                        submit()
                    }
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)

        views.ssssPassphraseEnterEdittext.textChanges()
                .onEach {
                    
                    sharedViewModel.handle(BootstrapActions.UpdateCandidatePassphrase(it.toString()))
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)

        sharedViewModel.observeViewEvents {
            
        }

        views.bootstrapSubmit.debouncedClicks { submit() }

        withState(sharedViewModel) { state ->
            if (state.step !is BootstrapStep.SetupPassphrase) {
                return@withState
            }
            state.passphrase?.let {
                views.ssssPassphraseEnterEdittext.setFocusable(false);
                views.ssssPassphraseEnterEdittext.isFocusableInTouchMode = false
                views.ssssPassphraseEnterEdittext.setText(it)
            }

        }
    }

    private fun submit() = withState(sharedViewModel) { state ->
        if (state.step !is BootstrapStep.SetupPassphrase) {
            return@withState
        }
        val score = state.passphraseStrength.invoke()?.score
        val passphrase = views.ssssPassphraseEnterEdittext.text?.toString()
        if (passphrase.isNullOrBlank()) {
            views.ssssPassphraseEnterTil.error = getString(R.string.passphrase_empty_error_message)
        } else if (score != 4) {
            views.ssssPassphraseEnterTil.error = getString(R.string.passphrase_passphrase_too_weak)
        } else {
            sharedViewModel.handle(BootstrapActions.GoToConfirmPassphrase(passphrase))
        }
    }

    override fun invalidate() = withState(sharedViewModel) { state ->
        if (state.step is BootstrapStep.SetupPassphrase) {
            state.passphraseStrength.invoke()?.let { strength ->
                val score = strength.score
                views.ssssPassphraseSecurityProgress.strength = score
                if (score in 1..3) {
                    val hint =
                            strength.feedback?.getWarning(VectorLocale.applicationLocale)?.takeIf { it.isNotBlank() }
                                    ?: strength.feedback?.getSuggestions(VectorLocale.applicationLocale)?.firstOrNull()
                    if (hint != null && hint != views.ssssPassphraseEnterTil.error.toString()) {
                        views.ssssPassphraseEnterTil.error = hint
                    }
                } else {
                    views.ssssPassphraseEnterTil.error = null
                }
            }
        }
    }
}
