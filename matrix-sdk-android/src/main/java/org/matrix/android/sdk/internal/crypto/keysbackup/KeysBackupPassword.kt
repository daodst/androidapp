


package org.matrix.android.sdk.internal.crypto.keysbackup

import androidx.annotation.WorkerThread
import org.matrix.android.sdk.api.listeners.ProgressListener
import timber.log.Timber
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

private const val SALT_LENGTH = 32
private const val DEFAULT_ITERATION = 500_000

internal data class GeneratePrivateKeyResult(
        
        val privateKey: ByteArray,
        
        val salt: String,
        
        val iterations: Int
)


@WorkerThread
internal fun generatePrivateKeyWithPassword(password: String,
                                            progressListener: ProgressListener?
): GeneratePrivateKeyResult {
    val salt = generateSalt()
    val iterations = DEFAULT_ITERATION
    val privateKey = deriveKey(password, salt, iterations, progressListener)

    return GeneratePrivateKeyResult(privateKey, salt, iterations)
}


@WorkerThread
internal fun retrievePrivateKeyWithPassword(password: String,
                                            salt: String,
                                            iterations: Int,
                                            progressListener: ProgressListener? = null): ByteArray {
    return deriveKey(password, salt, iterations, progressListener)
}


@WorkerThread
internal fun deriveKey(password: String,
                       salt: String,
                       iterations: Int,
                       progressListener: ProgressListener?): ByteArray {
    
    val t0 = System.currentTimeMillis()

    
    
    

    
    
    val prf = Mac.getInstance("HmacSHA512")

    prf.init(SecretKeySpec(password.toByteArray(), "HmacSHA512"))

    
    val dk = ByteArray(32)
    val uc = ByteArray(64)

    
    prf.update(salt.toByteArray())
    val int32BE = byteArrayOf(0, 0, 0, 1)
    prf.update(int32BE)
    prf.doFinal(uc, 0)

    
    System.arraycopy(uc, 0, dk, 0, dk.size)

    var lastProgress = -1

    for (index in 2..iterations) {
        
        prf.update(uc)
        prf.doFinal(uc, 0)

        
        for (byteIndex in dk.indices) {
            dk[byteIndex] = dk[byteIndex] xor uc[byteIndex]
        }

        val progress = (index + 1) * 100 / iterations
        if (progress != lastProgress) {
            lastProgress = progress
            progressListener?.onProgress(lastProgress, 100)
        }
    }

    Timber.v("KeysBackupPassword: deriveKeys() : " + iterations + " in " + (System.currentTimeMillis() - t0) + " ms")

    return dk
}


private fun generateSalt(): String {
    val salt = buildString {
        do {
            append(UUID.randomUUID().toString())
        } while (length < SALT_LENGTH)
    }

    return salt.substring(0, SALT_LENGTH)
}
