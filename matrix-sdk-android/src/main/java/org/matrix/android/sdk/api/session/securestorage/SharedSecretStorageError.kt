

package org.matrix.android.sdk.api.session.securestorage

sealed class SharedSecretStorageError(message: String?) : Throwable(message) {
    data class UnknownSecret(val secretName: String) : SharedSecretStorageError("Unknown Secret $secretName")
    data class UnknownKey(val keyId: String) : SharedSecretStorageError("Unknown key $keyId")
    data class UnknownAlgorithm(val keyId: String) : SharedSecretStorageError("Unknown algorithm $keyId")
    data class UnsupportedAlgorithm(val algorithm: String) : SharedSecretStorageError("Unknown algorithm $algorithm")
    data class SecretNotEncrypted(val secretName: String) : SharedSecretStorageError("Missing content for secret $secretName")
    data class SecretNotEncryptedWithKey(val secretName: String, val keyId: String) :
            SharedSecretStorageError("Missing content for secret $secretName with key $keyId")

    object BadKeyFormat : SharedSecretStorageError("Bad Key Format")
    object ParsingError : SharedSecretStorageError("parsing Error")
    object BadMac : SharedSecretStorageError("Bad mac")
    object BadCipherText : SharedSecretStorageError("Bad cipher text")

    data class OtherError(val reason: Throwable) : SharedSecretStorageError(reason.localizedMessage)
}
