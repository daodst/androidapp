

package org.matrix.android.sdk.api.session.crypto.verification

interface VerificationTransaction {

    var state: VerificationTxState

    val transactionId: String
    val otherUserId: String
    var otherDeviceId: String?

    
    val isIncoming: Boolean

    
    fun cancel()

    fun cancel(code: CancelCode)

    fun isToDeviceTransport(): Boolean
}
