

package org.matrix.android.sdk.internal.session.securestorage

import org.matrix.android.sdk.api.session.securestorage.SecureStorageService
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

internal class DefaultSecureStorageService @Inject constructor(private val secretStoringUtils: SecretStoringUtils) : SecureStorageService {

    override fun securelyStoreObject(any: Any, keyAlias: String, outputStream: OutputStream) {
        secretStoringUtils.securelyStoreObject(any, keyAlias, outputStream)
    }

    override fun <T> loadSecureSecret(inputStream: InputStream, keyAlias: String): T? {
        return secretStoringUtils.loadSecureSecret(inputStream, keyAlias)
    }
}
