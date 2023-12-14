

package org.matrix.android.sdk.api.session.securestorage

sealed class KeyInfoResult {
    data class Success(val keyInfo: KeyInfo) : KeyInfoResult()
    data class Error(val error: SharedSecretStorageError) : KeyInfoResult()

    fun isSuccess(): Boolean = this is Success
}
