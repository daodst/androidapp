

package org.matrix.android.sdk.api.session.securestorage

import java.io.InputStream
import java.io.OutputStream

interface SecureStorageService {

    fun securelyStoreObject(any: Any, keyAlias: String, outputStream: OutputStream)

    fun <T> loadSecureSecret(inputStream: InputStream, keyAlias: String): T?
}
