

package im.vector.app.core.extensions

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import im.vector.app.core.services.VectorSyncService
import im.vector.app.features.session.VectorSessionStore
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.sync.FilterService
import timber.log.Timber

fun Session.configureAndStart(context: Context, startSyncing: Boolean = true) {
    Timber.i("Configure and start session for $myUserId")
    open()
    setFilter(FilterService.FilterPreset.ElementFilter)
    if (startSyncing) {
        startSyncing(context)
    }
    refreshPushers()
    context.singletonEntryPoint().webRtcCallManager().checkForProtocolsSupportIfNeeded()
}

fun Session.startSyncing(context: Context) {
    val applicationContext = context.applicationContext
    if (!hasAlreadySynced()) {
        
        VectorSyncService.newOneShotIntent(
                context = applicationContext,
                sessionId = sessionId
        )
                .let {
                    try {
                        ContextCompat.startForegroundService(applicationContext, it)
                    } catch (ex: Throwable) {
                        
                        Timber.e(ex)
                    }
                }
    } else {
        val isAtLeastStarted = ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        Timber.v("--> is at least started? $isAtLeastStarted")
        startSync(isAtLeastStarted)
    }
}


fun Session.hasUnsavedKeys(): Boolean {
    return cryptoService().inboundGroupSessionsCount(false) > 0 &&
            cryptoService().keysBackupService().state != KeysBackupState.ReadyToBackUp
}

fun Session.cannotLogoutSafely(): Boolean {
    
    return hasUnsavedKeys() ||
            
            (cryptoService().crossSigningService().allPrivateKeysKnown() &&
                    
                    !sharedSecretStorageService.isRecoverySetup())
}

fun Session.vectorStore(context: Context) = VectorSessionStore(context, myUserId)
