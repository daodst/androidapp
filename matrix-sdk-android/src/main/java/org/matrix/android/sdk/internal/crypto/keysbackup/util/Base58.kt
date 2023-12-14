

package org.matrix.android.sdk.internal.crypto.keysbackup.util

import java.math.BigInteger


private const val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
private val BASE = BigInteger.valueOf(58)


internal fun base58encode(input: ByteArray): String {
    var bi = BigInteger(1, input)
    val s = StringBuffer()
    while (bi >= BASE) {
        val mod = bi.mod(BASE)
        s.insert(0, ALPHABET[mod.toInt()])
        bi = bi.subtract(mod).divide(BASE)
    }
    s.insert(0, ALPHABET[bi.toInt()])
    
    for (anInput in input) {
        if (anInput.toInt() == 0) {
            s.insert(0, ALPHABET[0])
        } else {
            break
        }
    }
    return s.toString()
}


internal fun base58decode(input: String): ByteArray {
    var result = decodeToBigInteger(input).toByteArray()

    
    if (result[0] == 0.toByte()) {
        result = result.copyOfRange(1, result.size)
    }

    return result
}

private fun decodeToBigInteger(input: String): BigInteger {
    var bi = BigInteger.valueOf(0)
    
    for (i in input.length - 1 downTo 0) {
        val alphaIndex = ALPHABET.indexOf(input[i])
        bi = bi.add(BigInteger.valueOf(alphaIndex.toLong()).multiply(BASE.pow(input.length - 1 - i)))
    }
    return bi
}
