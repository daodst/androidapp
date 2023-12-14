
package org.matrix.android.sdk.internal.crypto.tools

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.ceil


internal object HkdfSha256 {

    fun deriveSecret(inputKeyMaterial: ByteArray, salt: ByteArray?, info: ByteArray, outputLength: Int): ByteArray {
        return expand(extract(salt, inputKeyMaterial), info, outputLength)
    }

    
    private fun extract(salt: ByteArray?, ikm: ByteArray): ByteArray {
        val mac = initMac(salt ?: ByteArray(HASH_LEN) { 0.toByte() })
        return mac.doFinal(ikm)
    }

    
    private fun expand(prk: ByteArray, info: ByteArray = ByteArray(0), outputLength: Int): ByteArray {
        require(outputLength <= 255 * HASH_LEN) { "outputLength must be less than or equal to 255*HashLen" }

        
        val n = ceil(outputLength.toDouble() / HASH_LEN.toDouble()).toInt()

        var stepHash = ByteArray(0) 

        val generatedBytes = ByteArrayOutputStream() 
        val mac = initMac(prk)
        for (roundNum in 1..n) {
            mac.reset()
            val t = ByteBuffer.allocate(stepHash.size + info.size + 1).apply {
                put(stepHash)
                put(info)
                put(roundNum.toByte())
            }
            stepHash = mac.doFinal(t.array())
            generatedBytes.write(stepHash)
        }

        return generatedBytes.toByteArray().sliceArray(0 until outputLength)
    }

    private fun initMac(secret: ByteArray): Mac {
        val mac = Mac.getInstance(HASH_ALG)
        mac.init(SecretKeySpec(secret, HASH_ALG))
        return mac
    }

    private const val HASH_LEN = 32
    private const val HASH_ALG = "HmacSHA256"
}
