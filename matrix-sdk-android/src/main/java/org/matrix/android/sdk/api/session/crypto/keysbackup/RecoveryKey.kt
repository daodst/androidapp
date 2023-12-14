

package org.matrix.android.sdk.api.session.crypto.keysbackup

import org.matrix.android.sdk.internal.crypto.keysbackup.util.base58decode
import org.matrix.android.sdk.internal.crypto.keysbackup.util.base58encode
import kotlin.experimental.xor



private const val CHAR_0 = 0x8B.toByte()
private const val CHAR_1 = 0x01.toByte()

private const val RECOVERY_KEY_LENGTH = 2 + 32 + 1


fun isValidRecoveryKey(recoveryKey: String?): Boolean {
    return extractCurveKeyFromRecoveryKey(recoveryKey) != null
}


fun computeRecoveryKey(curve25519Key: ByteArray): String {
    
    val data = ByteArray(curve25519Key.size + 3)

    
    data[0] = CHAR_0
    data[1] = CHAR_1

    
    var parity: Byte = CHAR_0 xor CHAR_1

    for (i in curve25519Key.indices) {
        data[i + 2] = curve25519Key[i]
        parity = parity xor curve25519Key[i]
    }

    
    data[curve25519Key.size + 2] = parity

    
    return base58encode(data)
}


fun extractCurveKeyFromRecoveryKey(recoveryKey: String?): ByteArray? {
    if (recoveryKey == null) {
        return null
    }

    
    val spaceFreeRecoveryKey = recoveryKey.replace("""\s""".toRegex(), "")

    val b58DecodedKey = base58decode(spaceFreeRecoveryKey)

    
    if (b58DecodedKey.size != RECOVERY_KEY_LENGTH) {
        return null
    }

    
    if (b58DecodedKey[0] != CHAR_0) {
        return null
    }

    
    if (b58DecodedKey[1] != CHAR_1) {
        return null
    }

    
    var parity: Byte = 0

    for (i in 0 until RECOVERY_KEY_LENGTH) {
        parity = parity xor b58DecodedKey[i]
    }

    if (parity != 0.toByte()) {
        return null
    }

    
    val result = ByteArray(b58DecodedKey.size - 3)

    for (i in 2 until b58DecodedKey.size - 1) {
        result[i - 2] = b58DecodedKey[i]
    }

    return result
}
