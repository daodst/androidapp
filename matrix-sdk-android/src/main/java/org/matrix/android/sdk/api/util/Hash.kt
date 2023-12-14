

package org.matrix.android.sdk.api.util

import java.security.MessageDigest
import java.util.Locale


fun String.md5() = try {
    val digest = MessageDigest.getInstance("md5")
    digest.update(toByteArray())
    digest.digest()
            .joinToString("") { String.format("%02X", it) }
            .lowercase(Locale.ROOT)
} catch (exc: Exception) {
    
    hashCode().toString()
}
