

package im.vector.app.features.crypto.recover

import im.vector.app.R
import im.vector.app.core.platform.ViewModelTask
import im.vector.app.core.platform.WaitingViewData
import im.vector.app.core.resources.StringProvider
import org.matrix.android.sdk.api.NoOpMatrixCallback
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.KEYBACKUP_SECRET_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.keysbackup.computeRecoveryKey
import org.matrix.android.sdk.api.session.crypto.keysbackup.extractCurveKeyFromRecoveryKey
import org.matrix.android.sdk.api.session.securestorage.EmptyKeySigner
import org.matrix.android.sdk.api.session.securestorage.RawBytesKeySpec
import org.matrix.android.sdk.api.session.securestorage.SharedSecretStorageService
import org.matrix.android.sdk.api.session.securestorage.SsssKeyCreationInfo
import org.matrix.android.sdk.api.util.awaitCallback
import org.matrix.android.sdk.api.util.toBase64NoPadding
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class BackupToQuadSMigrationTask @Inject constructor(
        val session: Session,
        val stringProvider: StringProvider
) : ViewModelTask<BackupToQuadSMigrationTask.Params, BackupToQuadSMigrationTask.Result> {

    sealed class Result {
        object Success : Result()
        abstract class Failure(val throwable: Throwable?) : Result()
        object InvalidRecoverySecret : Failure(null)
        object NoKeyBackupVersion : Failure(null)
        object IllegalParams : Failure(null)
        class ErrorFailure(throwable: Throwable) : Failure(throwable)
    }

    data class Params(
            val passphrase: String?,
            val recoveryKey: String?,
            val progressListener: BootstrapProgressListener? = null
    )

    override suspend fun execute(params: Params): Result {
        try {
            
            
            val keysBackupService = session.cryptoService().keysBackupService()
            val quadS = session.sharedSecretStorageService

            val version = keysBackupService.keysBackupVersion ?: return Result.NoKeyBackupVersion

            reportProgress(params, R.string.bootstrap_progress_checking_backup)
            val curveKey =
                    (if (params.recoveryKey != null) {
                        extractCurveKeyFromRecoveryKey(params.recoveryKey)
                    } else if (!params.passphrase.isNullOrEmpty() && version.getAuthDataAsMegolmBackupAuthData()?.privateKeySalt != null) {
                        version.getAuthDataAsMegolmBackupAuthData()?.let { authData ->
                            keysBackupService.computePrivateKey(
                                    params.passphrase,
                                    authData.privateKeySalt!!,
                                    authData.privateKeyIterations!!,
                                    object : ProgressListener {
                                        override fun onProgress(progress: Int, total: Int) {
                                            params.progressListener?.onProgress(WaitingViewData(
                                                    stringProvider.getString(R.string.bootstrap_progress_checking_backup_with_info,
                                                            "$progress/$total")
                                            ))
                                        }
                                    })
                        }
                    } else null)
                            ?: return Result.IllegalParams

            reportProgress(params, R.string.bootstrap_progress_compute_curve_key)
            val recoveryKey = computeRecoveryKey(curveKey)

            val isValid = awaitCallback<Boolean> {
                keysBackupService.isValidRecoveryKeyForCurrentVersion(recoveryKey, it)
            }

            if (!isValid) return Result.InvalidRecoverySecret

            val info: SsssKeyCreationInfo =
                    when {
                        params.passphrase?.isNotEmpty() == true -> {
                            reportProgress(params, R.string.bootstrap_progress_generating_ssss)
                            quadS.generateKeyWithPassphrase(
                                    UUID.randomUUID().toString(),
                                    "ssss_key",
                                    params.passphrase,
                                    EmptyKeySigner(),
                                    object : ProgressListener {
                                        override fun onProgress(progress: Int, total: Int) {
                                            params.progressListener?.onProgress(
                                                    WaitingViewData(
                                                            stringProvider.getString(
                                                                    R.string.bootstrap_progress_generating_ssss_with_info,
                                                                    "$progress/$total")
                                                    ))
                                        }
                                    }
                            )
                        }
                        params.recoveryKey != null              -> {
                            reportProgress(params, R.string.bootstrap_progress_generating_ssss_recovery)
                            quadS.generateKey(
                                    UUID.randomUUID().toString(),
                                    extractCurveKeyFromRecoveryKey(params.recoveryKey)?.let { RawBytesKeySpec(it) },
                                    "ssss_key",
                                    EmptyKeySigner()
                            )
                        }
                        else                                    -> {
                            return Result.IllegalParams
                        }
                    }

            
            
            reportProgress(params, R.string.bootstrap_progress_storing_in_sss)
            quadS.storeSecret(
                    KEYBACKUP_SECRET_SSSS_NAME,
                    curveKey.toBase64NoPadding(),
                    listOf(SharedSecretStorageService.KeyRef(info.keyId, info.keySpec))
            )

            
            keysBackupService.saveBackupRecoveryKey(recoveryKey, version.version)

            
            session.cryptoService().keysBackupService().restoreKeysWithRecoveryKey(
                    version,
                    recoveryKey,
                    null,
                    null,
                    null,
                    NoOpMatrixCallback()
            )

            return Result.Success
        } catch (failure: Throwable) {
            Timber.e(failure, "## BackupToQuadSMigrationTask - Failed to migrate backup")
            return Result.ErrorFailure(failure)
        }
    }

    private fun reportProgress(params: Params, stringRes: Int) {
        params.progressListener?.onProgress(WaitingViewData(stringProvider.getString(stringRes)))
    }
}
