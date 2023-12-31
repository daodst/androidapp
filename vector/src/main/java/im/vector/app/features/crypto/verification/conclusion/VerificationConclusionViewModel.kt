
package im.vector.app.features.crypto.verification.conclusion

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import im.vector.app.core.platform.EmptyAction
import im.vector.app.core.platform.EmptyViewEvents
import im.vector.app.core.platform.VectorViewModel
import org.matrix.android.sdk.api.session.crypto.verification.CancelCode
import org.matrix.android.sdk.api.session.crypto.verification.safeValueOf

data class VerificationConclusionViewState(
        val conclusionState: ConclusionState = ConclusionState.CANCELLED,
        val isSelfVerification: Boolean = false
) : MavericksState

enum class ConclusionState {
    SUCCESS,
    WARNING,
    CANCELLED
}

class VerificationConclusionViewModel(initialState: VerificationConclusionViewState) :
        VectorViewModel<VerificationConclusionViewState, EmptyAction, EmptyViewEvents>(initialState) {

    companion object : MavericksViewModelFactory<VerificationConclusionViewModel, VerificationConclusionViewState> {

        override fun initialState(viewModelContext: ViewModelContext): VerificationConclusionViewState? {
            val args = viewModelContext.args<VerificationConclusionFragment.Args>()

            return when (safeValueOf(args.cancelReason)) {
                CancelCode.QrCodeInvalid,
                CancelCode.MismatchedUser,
                CancelCode.MismatchedSas,
                CancelCode.MismatchedCommitment,
                CancelCode.MismatchedKeys -> {
                    VerificationConclusionViewState(ConclusionState.WARNING, args.isMe)
                }
                else                      -> {
                    VerificationConclusionViewState(
                            if (args.isSuccessFull) ConclusionState.SUCCESS else ConclusionState.CANCELLED,
                            args.isMe
                    )
                }
            }
        }
    }

    override fun handle(action: EmptyAction) {}
}
