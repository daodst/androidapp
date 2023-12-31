

package org.matrix.android.sdk.api.session.crypto.verification

enum class CancelCode(val value: String, val humanReadable: String) {
    User("m.user", "the user cancelled the verification"),
    Timeout("m.timeout", "the verification process timed out"),
    UnknownTransaction("m.unknown_transaction", "the device does not know about that transaction"),
    UnknownMethod("m.unknown_method", "the device can’t agree on a key agreement, hash, MAC, or SAS method"),
    MismatchedCommitment("m.mismatched_commitment", "the hash commitment did not match"),
    MismatchedSas("m.mismatched_sas", "the SAS did not match"),
    UnexpectedMessage("m.unexpected_message", "the device received an unexpected message"),
    InvalidMessage("m.invalid_message", "an invalid message was received"),
    MismatchedKeys("m.key_mismatch", "Key mismatch"),
    UserError("m.user_error", "User error"),
    MismatchedUser("m.user_mismatch", "User mismatch"),
    QrCodeInvalid("m.qr_code.invalid", "Invalid QR code")
}

fun safeValueOf(code: String?): CancelCode {
    return CancelCode.values().firstOrNull { code == it.value } ?: CancelCode.User
}
