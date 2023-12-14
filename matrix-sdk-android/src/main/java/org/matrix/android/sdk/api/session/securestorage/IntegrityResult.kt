

package org.matrix.android.sdk.api.session.securestorage

sealed class IntegrityResult {
    data class Success(val passphraseBased: Boolean) : IntegrityResult()
    data class Error(val cause: SharedSecretStorageError) : IntegrityResult()
}
