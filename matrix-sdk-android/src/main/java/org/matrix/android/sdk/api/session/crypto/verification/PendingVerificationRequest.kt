
package org.matrix.android.sdk.api.session.crypto.verification

import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.internal.crypto.model.rest.VERIFICATION_METHOD_QR_CODE_SCAN
import org.matrix.android.sdk.internal.crypto.model.rest.VERIFICATION_METHOD_QR_CODE_SHOW
import org.matrix.android.sdk.internal.crypto.model.rest.VERIFICATION_METHOD_SAS
import java.util.UUID


data class PendingVerificationRequest(
        val ageLocalTs: Long,
        val isIncoming: Boolean = false,
        val localId: String = UUID.randomUUID().toString(),
        val otherUserId: String,
        val roomId: String?,
        val transactionId: String? = null,
        val requestInfo: ValidVerificationInfoRequest? = null,
        val readyInfo: ValidVerificationInfoReady? = null,
        val cancelConclusion: CancelCode? = null,
        val isSuccessful: Boolean = false,
        val handledByOtherSession: Boolean = false,
        
        val targetDevices: List<String>? = null
) {
    val isReady: Boolean = readyInfo != null
    val isSent: Boolean = transactionId != null

    val isFinished: Boolean = isSuccessful || cancelConclusion != null

    
    fun isSasSupported(): Boolean {
        return requestInfo?.methods?.contains(VERIFICATION_METHOD_SAS).orFalse() &&
                readyInfo?.methods?.contains(VERIFICATION_METHOD_SAS).orFalse()
    }

    
    fun otherCanShowQrCode(): Boolean {
        return if (isIncoming) {
            requestInfo?.methods?.contains(VERIFICATION_METHOD_QR_CODE_SHOW).orFalse() &&
                    readyInfo?.methods?.contains(VERIFICATION_METHOD_QR_CODE_SCAN).orFalse()
        } else {
            requestInfo?.methods?.contains(VERIFICATION_METHOD_QR_CODE_SCAN).orFalse() &&
                    readyInfo?.methods?.contains(VERIFICATION_METHOD_QR_CODE_SHOW).orFalse()
        }
    }

    
    fun otherCanScanQrCode(): Boolean {
        return if (isIncoming) {
            requestInfo?.methods?.contains(VERIFICATION_METHOD_QR_CODE_SCAN).orFalse() &&
                    readyInfo?.methods?.contains(VERIFICATION_METHOD_QR_CODE_SHOW).orFalse()
        } else {
            requestInfo?.methods?.contains(VERIFICATION_METHOD_QR_CODE_SHOW).orFalse() &&
                    readyInfo?.methods?.contains(VERIFICATION_METHOD_QR_CODE_SCAN).orFalse()
        }
    }
}
