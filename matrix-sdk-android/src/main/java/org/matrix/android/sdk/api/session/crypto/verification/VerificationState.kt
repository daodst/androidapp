

package org.matrix.android.sdk.api.session.crypto.verification

enum class VerificationState {
    REQUEST,
    WAITING,
    CANCELED_BY_ME,
    CANCELED_BY_OTHER,
    DONE
}

fun VerificationState.isCanceled(): Boolean {
    return this == VerificationState.CANCELED_BY_ME || this == VerificationState.CANCELED_BY_OTHER
}
