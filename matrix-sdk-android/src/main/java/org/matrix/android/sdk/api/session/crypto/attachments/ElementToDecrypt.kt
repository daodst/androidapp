

package org.matrix.android.sdk.api.session.crypto.attachments

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo

fun EncryptedFileInfo.toElementToDecrypt(): ElementToDecrypt? {
    
    if (isValid()) {
        
        return ElementToDecrypt(
                iv = this.iv ?: "",
                k = this.key?.k ?: "",
                sha256 = this.hashes?.get("sha256") ?: ""
        )
    }

    return null
}


@Parcelize
data class ElementToDecrypt(
        val iv: String,
        val k: String,
        val sha256: String
) : Parcelable
