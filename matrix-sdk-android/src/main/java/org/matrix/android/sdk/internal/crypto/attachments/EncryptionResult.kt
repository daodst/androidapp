

package org.matrix.android.sdk.internal.crypto.attachments

import org.matrix.android.sdk.api.session.crypto.model.EncryptedFileInfo


internal data class EncryptionResult(
        val encryptedFileInfo: EncryptedFileInfo,
        val encryptedByteArray: ByteArray
)
