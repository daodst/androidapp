

package org.matrix.android.sdk.api.session.crypto.keysbackup

sealed interface KeysBackupLastVersionResult {
    
    object NoKeysBackup : KeysBackupLastVersionResult
    data class KeysBackup(val keysVersionResult: KeysVersionResult) : KeysBackupLastVersionResult
}

fun KeysBackupLastVersionResult.toKeysVersionResult(): KeysVersionResult? = when (this) {
    is KeysBackupLastVersionResult.KeysBackup -> keysVersionResult
    KeysBackupLastVersionResult.NoKeysBackup  -> null
}
