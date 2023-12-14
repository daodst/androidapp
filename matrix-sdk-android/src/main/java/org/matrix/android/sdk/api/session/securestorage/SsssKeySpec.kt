

package org.matrix.android.sdk.api.session.securestorage

import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.session.crypto.keysbackup.extractCurveKeyFromRecoveryKey
import org.matrix.android.sdk.internal.crypto.keysbackup.deriveKey


interface SsssKeySpec

data class RawBytesKeySpec(
        val privateKey: ByteArray
) : SsssKeySpec {

    companion object {

        fun fromPassphrase(passphrase: String, salt: String, iterations: Int, progressListener: ProgressListener?): RawBytesKeySpec {
            return RawBytesKeySpec(
                    privateKey = deriveKey(
                            passphrase,
                            salt,
                            iterations,
                            progressListener
                    )
            )
        }

        fun fromRecoveryKey(recoveryKey: String): RawBytesKeySpec? {
            return extractCurveKeyFromRecoveryKey(recoveryKey)?.let {
                RawBytesKeySpec(
                        privateKey = it
                )
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RawBytesKeySpec

        if (!privateKey.contentEquals(other.privateKey)) return false

        return true
    }

    override fun hashCode(): Int {
        return privateKey.contentHashCode()
    }
}
