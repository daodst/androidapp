

package org.matrix.android.sdk.internal.session.contentscanner

import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileKey
import org.matrix.android.sdk.internal.crypto.tools.withOlmEncryption
import org.matrix.android.sdk.internal.session.contentscanner.model.DownloadBody
import org.matrix.android.sdk.internal.session.contentscanner.model.EncryptedBody
import org.matrix.android.sdk.internal.session.contentscanner.model.toCanonicalJson

internal object ScanEncryptorUtils {

    fun getDownloadBodyAndEncryptIfNeeded(publicServerKey: String?, mxcUrl: String, elementToDecrypt: ElementToDecrypt): DownloadBody {
        
        
        val encryptedInfo = EncryptedFileInfo(
                url = mxcUrl,
                iv = elementToDecrypt.iv,
                hashes = mapOf("sha256" to elementToDecrypt.sha256),
                key = EncryptedFileKey(
                        k = elementToDecrypt.k,
                        alg = "A256CTR",
                        keyOps = listOf("encrypt", "decrypt"),
                        kty = "oct",
                        ext = true
                ),
                v = "v2"
        )
        return if (publicServerKey != null) {
            
            withOlmEncryption { olm ->
                olm.setRecipientKey(publicServerKey)

                val olmResult = olm.encrypt(DownloadBody(encryptedInfo).toCanonicalJson())
                DownloadBody(
                        encryptedBody = EncryptedBody(
                                cipherText = olmResult.mCipherText,
                                ephemeral = olmResult.mEphemeralKey,
                                mac = olmResult.mMac
                        )
                )
            }
        } else {
            DownloadBody(encryptedInfo)
        }
    }
}
