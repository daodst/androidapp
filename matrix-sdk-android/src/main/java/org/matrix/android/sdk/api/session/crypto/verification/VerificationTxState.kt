

package org.matrix.android.sdk.api.session.crypto.verification

sealed class VerificationTxState {
    
    object None : VerificationTxState()

    
    abstract class VerificationSasTxState : VerificationTxState()

    object SendingStart : VerificationSasTxState()
    object Started : VerificationSasTxState()
    object OnStarted : VerificationSasTxState()
    object SendingAccept : VerificationSasTxState()
    object Accepted : VerificationSasTxState()
    object OnAccepted : VerificationSasTxState()
    object SendingKey : VerificationSasTxState()
    object KeySent : VerificationSasTxState()
    object OnKeyReceived : VerificationSasTxState()
    object ShortCodeReady : VerificationSasTxState()
    object ShortCodeAccepted : VerificationSasTxState()
    object SendingMac : VerificationSasTxState()
    object MacSent : VerificationSasTxState()
    object Verifying : VerificationSasTxState()

    
    abstract class VerificationQrTxState : VerificationTxState()

    
    object QrScannedByOther : VerificationQrTxState()
    object WaitingOtherReciprocateConfirm : VerificationQrTxState()

    
    abstract class TerminalTxState : VerificationTxState()

    object Verified : TerminalTxState()

    
    data class Cancelled(val cancelCode: CancelCode, val byMe: Boolean) : TerminalTxState()
}
