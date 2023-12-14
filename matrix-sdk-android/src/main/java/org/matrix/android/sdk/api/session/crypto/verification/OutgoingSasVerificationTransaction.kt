

package org.matrix.android.sdk.api.session.crypto.verification

interface OutgoingSasVerificationTransaction : SasVerificationTransaction {
    val uxState: UxState

    enum class UxState {
        UNKNOWN,
        WAIT_FOR_START,
        WAIT_FOR_KEY_AGREEMENT,
        SHOW_SAS,
        WAIT_FOR_VERIFICATION,
        VERIFIED,
        CANCELLED_BY_ME,
        CANCELLED_BY_OTHER
    }
}
