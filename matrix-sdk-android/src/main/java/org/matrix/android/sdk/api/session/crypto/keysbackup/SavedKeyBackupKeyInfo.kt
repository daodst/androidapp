

package org.matrix.android.sdk.api.session.crypto.keysbackup

data class SavedKeyBackupKeyInfo(
        val recoveryKey: String,
        val version: String
)
