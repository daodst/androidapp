

package org.matrix.android.sdk.api.session.crypto.model

data class ImportRoomKeysResult(
        val totalNumberOfKeys: Int,
        val successfullyNumberOfImportedKeys: Int
)
