

package org.matrix.android.sdk.api.session.crypto.crosssigning

sealed class UserTrustResult {
    object Success : UserTrustResult()

    
    
    data class CrossSigningNotConfigured(val userID: String) : UserTrustResult()

    data class UnknownCrossSignatureInfo(val userID: String) : UserTrustResult()
    data class KeysNotTrusted(val key: MXCrossSigningInfo) : UserTrustResult()
    data class KeyNotSigned(val key: CryptoCrossSigningKey) : UserTrustResult()
    data class InvalidSignature(val key: CryptoCrossSigningKey, val signature: String) : UserTrustResult()
}

fun UserTrustResult.isVerified() = this is UserTrustResult.Success
