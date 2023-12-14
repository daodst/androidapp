
package org.matrix.android.sdk.internal.crypto.crosssigning

import android.util.Base64
import org.matrix.android.sdk.api.session.crypto.crosssigning.CryptoCrossSigningKey
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.internal.util.JsonCanonicalizer
import timber.log.Timber

internal fun CryptoDeviceInfo.canonicalSignable(): String {
    return JsonCanonicalizer.getCanonicalJson(Map::class.java, signalableJSONDictionary())
}

internal fun CryptoCrossSigningKey.canonicalSignable(): String {
    return JsonCanonicalizer.getCanonicalJson(Map::class.java, signalableJSONDictionary())
}


internal fun String.fromBase64Safe(): ByteArray? {
    return try {
        Base64.decode(this, Base64.DEFAULT)
    } catch (throwable: Throwable) {
        Timber.e(throwable, "Unable to decode base64 string")
        null
    }
}
