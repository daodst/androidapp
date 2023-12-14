

package org.matrix.android.sdk.internal.crypto.verification.qrcode

import org.matrix.android.sdk.api.util.toBase64NoPadding
import java.security.SecureRandom

internal fun generateSharedSecretV2(): String {
    val secureRandom = SecureRandom()

    
    val secretBytes = ByteArray(8)
    secureRandom.nextBytes(secretBytes)
    return secretBytes.toBase64NoPadding()
}
