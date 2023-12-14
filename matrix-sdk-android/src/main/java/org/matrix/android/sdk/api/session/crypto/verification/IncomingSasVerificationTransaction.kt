

package org.matrix.android.sdk.api.session.crypto.verification

interface IncomingSasVerificationTransaction : SasVerificationTransaction {
    val uxState: UxState

    fun performAccept()

    enum class UxState {
        UNKNOWN,
        SHOW_ACCEPT,
        WAIT_FOR_KEY_AGREEMENT,
        SHOW_SAS,
        WAIT_FOR_VERIFICATION,
        VERIFIED,
        CANCELLED_BY_ME,
        CANCELLED_BY_OTHER
    }
}
