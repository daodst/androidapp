

package org.matrix.android.sdk.api.session.crypto.keysbackup

import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.listeners.StepProgressListener
import org.matrix.android.sdk.api.session.crypto.model.ImportRoomKeysResult

interface KeysBackupService {
    
    fun getCurrentVersion(callback: MatrixCallback<KeysBackupLastVersionResult>)

    
    fun createKeysBackupVersion(keysBackupCreationInfo: MegolmBackupCreationInfo,
                                callback: MatrixCallback<KeysVersion>)

    
    fun getTotalNumbersOfKeys(): Int

    
    fun getTotalNumbersOfBackedUpKeys(): Int

    
    fun backupAllGroupSessions(progressListener: ProgressListener?,
                               callback: MatrixCallback<Unit>?)

    
    fun getKeysBackupTrust(keysBackupVersion: KeysVersionResult,
                           callback: MatrixCallback<KeysBackupVersionTrust>)

    
    fun getBackupProgress(progressListener: ProgressListener)

    
    fun getVersion(version: String,
                   callback: MatrixCallback<KeysVersionResult?>)

    
    fun forceUsingLastVersion(callback: MatrixCallback<Boolean>)

    
    fun checkAndStartKeysBackup()

    fun addListener(listener: KeysBackupStateListener)

    fun removeListener(listener: KeysBackupStateListener)

    
    fun prepareKeysBackupVersion(password: String?,
                                 progressListener: ProgressListener?,
                                 callback: MatrixCallback<MegolmBackupCreationInfo>)

    
    fun deleteBackup(version: String,
                     callback: MatrixCallback<Unit>?)

    
    fun canRestoreKeys(): Boolean

    
    fun trustKeysBackupVersion(keysBackupVersion: KeysVersionResult,
                               trust: Boolean,
                               callback: MatrixCallback<Unit>)

    
    fun trustKeysBackupVersionWithRecoveryKey(keysBackupVersion: KeysVersionResult,
                                              recoveryKey: String,
                                              callback: MatrixCallback<Unit>)

    
    fun trustKeysBackupVersionWithPassphrase(keysBackupVersion: KeysVersionResult,
                                             password: String,
                                             callback: MatrixCallback<Unit>)

    fun onSecretKeyGossip(secret: String)

    
    fun restoreKeysWithRecoveryKey(keysVersionResult: KeysVersionResult,
                                   recoveryKey: String, roomId: String?,
                                   sessionId: String?,
                                   stepProgressListener: StepProgressListener?,
                                   callback: MatrixCallback<ImportRoomKeysResult>)

    
    fun restoreKeyBackupWithPassword(keysBackupVersion: KeysVersionResult,
                                     password: String,
                                     roomId: String?,
                                     sessionId: String?,
                                     stepProgressListener: StepProgressListener?,
                                     callback: MatrixCallback<ImportRoomKeysResult>)

    val keysBackupVersion: KeysVersionResult?
    val currentBackupVersion: String?
    val isEnabled: Boolean
    val isStucked: Boolean
    val state: KeysBackupState

    
    fun saveBackupRecoveryKey(recoveryKey: String?, version: String?)
    fun getKeyBackupRecoveryKeyInfo(): SavedKeyBackupKeyInfo?

    fun isValidRecoveryKeyForCurrentVersion(recoveryKey: String, callback: MatrixCallback<Boolean>)

    fun computePrivateKey(passphrase: String,
                          privateKeySalt: String,
                          privateKeyIterations: Int,
                          progressListener: ProgressListener): ByteArray
}
