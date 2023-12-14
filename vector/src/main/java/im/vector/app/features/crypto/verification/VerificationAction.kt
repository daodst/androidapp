

package im.vector.app.features.crypto.verification

import im.vector.app.core.platform.VectorViewModelAction

sealed class VerificationAction : VectorViewModelAction {
    data class RequestVerificationByDM(val otherUserId: String, val roomId: String?) : VerificationAction()
    data class StartSASVerification(val otherUserId: String, val pendingRequestTransactionId: String) : VerificationAction()
    data class RemoteQrCodeScanned(val otherUserId: String, val transactionId: String, val scannedData: String) : VerificationAction()
    object OtherUserScannedSuccessfully : VerificationAction()
    object OtherUserDidNotScanned : VerificationAction()
    data class SASMatchAction(val otherUserId: String, val sasTransactionId: String) : VerificationAction()
    data class SASDoNotMatchAction(val otherUserId: String, val sasTransactionId: String) : VerificationAction()
    object GotItConclusion : VerificationAction()
    object SkipVerification : VerificationAction()
    object VerifyFromPassphrase : VerificationAction()
    data class GotResultFromSsss(val cypherData: String, val alias: String) : VerificationAction()
    object CancelledFromSsss : VerificationAction()
    object SecuredStorageHasBeenReset : VerificationAction()
}
