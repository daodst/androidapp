

package org.matrix.android.sdk.internal.crypto.verification.qrcode

import org.matrix.android.sdk.api.util.fromBase64
import org.matrix.android.sdk.api.util.toBase64NoPadding
import org.matrix.android.sdk.internal.extensions.toUnsignedInt

private val prefix = "MATRIX".toByteArray(Charsets.ISO_8859_1)

internal fun QrCodeData.toEncodedString(): String {
    var result = ByteArray(0)

    
    for (i in prefix.indices) {
        result += prefix[i]
    }

    
    result += 2

    
    result += when (this) {
        is QrCodeData.VerifyingAnotherUser             -> 0
        is QrCodeData.SelfVerifyingMasterKeyTrusted    -> 1
        is QrCodeData.SelfVerifyingMasterKeyNotTrusted -> 2
    }.toByte()

    
    val length = transactionId.length
    result += ((length and 0xFF00) shr 8).toByte()
    result += length.toByte()

    
    transactionId.forEach {
        result += it.code.toByte()
    }

    
    firstKey.fromBase64().forEach {
        result += it
    }
    secondKey.fromBase64().forEach {
        result += it
    }

    
    sharedSecret.fromBase64().forEach {
        result += it
    }

    return result.toString(Charsets.ISO_8859_1)
}

internal fun String.toQrCodeData(): QrCodeData? {
    val byteArray = toByteArray(Charsets.ISO_8859_1)

    

    
    
    if (byteArray.size < 10) return null

    for (i in prefix.indices) {
        if (byteArray[i] != prefix[i]) {
            return null
        }
    }

    var cursor = prefix.size 

    
    if (byteArray[cursor] != 2.toByte()) {
        return null
    }
    cursor++

    
    val mode = byteArray[cursor].toInt()
    cursor++

    
    val msb = byteArray[cursor].toUnsignedInt()
    val lsb = byteArray[cursor + 1].toUnsignedInt()

    val transactionLength = msb.shl(8) + lsb

    cursor++
    cursor++

    val secretLength = byteArray.size - 74 - transactionLength

    
    if (secretLength < 8) {
        return null
    }

    val transactionId = byteArray.copyOfRange(cursor, cursor + transactionLength).toString(Charsets.ISO_8859_1)
    cursor += transactionLength
    val key1 = byteArray.copyOfRange(cursor, cursor + 32).toBase64NoPadding()
    cursor += 32
    val key2 = byteArray.copyOfRange(cursor, cursor + 32).toBase64NoPadding()
    cursor += 32
    val secret = byteArray.copyOfRange(cursor, byteArray.size).toBase64NoPadding()

    return when (mode) {
        0    -> QrCodeData.VerifyingAnotherUser(transactionId, key1, key2, secret)
        1    -> QrCodeData.SelfVerifyingMasterKeyTrusted(transactionId, key1, key2, secret)
        2    -> QrCodeData.SelfVerifyingMasterKeyNotTrusted(transactionId, key1, key2, secret)
        else -> null
    }
}
