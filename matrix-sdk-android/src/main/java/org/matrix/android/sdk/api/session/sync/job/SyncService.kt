

package org.matrix.android.sdk.api.session.sync.job

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.isTokenError
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.sync.SyncState
import org.matrix.android.sdk.internal.network.NetworkConnectivityChecker
import org.matrix.android.sdk.internal.session.sync.SyncPresence
import org.matrix.android.sdk.internal.session.sync.SyncTask
import org.matrix.android.sdk.internal.task.TaskExecutor
import org.matrix.android.sdk.internal.util.BackgroundDetectionObserver
import timber.log.Timber
import java.net.SocketTimeoutException
import java.util.concurrent.atomic.AtomicBoolean


abstract class SyncService : Service() {

    private var sessionId: String? = null
    private var mIsSelfDestroyed: Boolean = false

    private var syncTimeoutSeconds: Int = getDefaultSyncTimeoutSeconds()
    private var syncDelaySeconds: Int = getDefaultSyncDelaySeconds()

    private var periodic: Boolean = false
    private var preventReschedule: Boolean = false

    private var isInitialSync: Boolean = false
    private lateinit var session: Session
    private lateinit var syncTask: SyncTask
    private lateinit var networkConnectivityChecker: NetworkConnectivityChecker
    private lateinit var taskExecutor: TaskExecutor
    private lateinit var coroutineDispatchers: MatrixCoroutineDispatchers
    private lateinit var backgroundDetectionObserver: BackgroundDetectionObserver

    private val isRunning = AtomicBoolean(false)

    private val serviceScope = CoroutineScope(SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.i("## Sync: onStartCommand [$this] $intent with action: ${intent?.action}")
        when (intent?.action) {
            ACTION_STOP -> {
                Timber.i("## Sync: stop command received")
                
                
                onStart(isInitialSync)
                
                preventReschedule = true
                
                if (!isInitialSync) {
                    stopMe()
                }
            }
            else        -> {
                val isInit = initialize(intent)
                onStart(isInitialSync)
                if (isInit) {
                    periodic = intent?.getBooleanExtra(EXTRA_PERIODIC, false) ?: false
                    val onNetworkBack = intent?.getBooleanExtra(EXTRA_NETWORK_BACK_RESTART, false) ?: false
                    Timber.d("## Sync: command received, periodic: $periodic  networkBack: $onNetworkBack")
                    if (!isInitialSync && onNetworkBack && !backgroundDetectionObserver.isInBackground) {
                        
                        
                        preventReschedule = true
                        stopMe()
                    } else {
                        
                        doSyncIfNotAlreadyRunning()
                    }
                } else {
                    Timber.d("## Sync: Failed to initialize service")
                    stopMe()
                }
            }
        }
        
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        Timber.i("## Sync: onDestroy() [$this] periodic:$periodic preventReschedule:$preventReschedule")
        if (!mIsSelfDestroyed) {
            Timber.d("## Sync: Destroy by the system : $this")
        }
        isRunning.set(false)
        
        serviceScope.coroutineContext.cancelChildren()
        if (!preventReschedule && periodic && sessionId != null && backgroundDetectionObserver.isInBackground) {
            Timber.d("## Sync: Reschedule service in $syncDelaySeconds sec")
            onRescheduleAsked(
                    sessionId = sessionId ?: "",
                    syncTimeoutSeconds = syncTimeoutSeconds,
                    syncDelaySeconds = syncDelaySeconds
            )
        }
        super.onDestroy()
    }

    private fun stopMe() {
        mIsSelfDestroyed = true
        stopSelf()
    }

    private fun doSyncIfNotAlreadyRunning() {
        if (isRunning.get()) {
            Timber.i("## Sync: Received a start while was already syncing... ignore")
        } else {
            isRunning.set(true)
            
            getSystemService<PowerManager>()?.run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "riotx:fdroidSynclock").apply {
                    acquire((syncTimeoutSeconds * 1000L + 10_000L))
                }
            }
            serviceScope.launch(coroutineDispatchers.io) {
                doSync()
            }
        }
    }

    private suspend fun doSync() {
        Timber.v("## Sync: Execute sync request with timeout $syncTimeoutSeconds seconds")
        val params = SyncTask.Params(syncTimeoutSeconds * 1000L, SyncPresence.Offline, afterPause = false)
        try {
            
            syncTask.execute(params)
            
            if (isInitialSync && session.getSyncState() == SyncState.Idle) {
                val isForeground = !backgroundDetectionObserver.isInBackground
                session.startSync(isForeground)
            }
            stopMe()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "## Sync: sync service did fail ${isRunning.get()}")
            if (throwable.isTokenError()) {
                
                preventReschedule = true
            }
            if (throwable is Failure.NetworkConnection) {
                
                if (throwable.cause is SocketTimeoutException) {
                    
                    
                    Timber.w("Timeout during sync, retry in loop")
                    doSync()
                    return
                }
                
                preventReschedule = true
                
                onNetworkError(
                        sessionId = sessionId ?: "",
                        syncTimeoutSeconds = syncTimeoutSeconds,
                        syncDelaySeconds = syncDelaySeconds,
                        isPeriodic = periodic
                )
            }
            
            if (isRunning.get()) stopMe()
        }
    }

    abstract fun provideMatrix(): Matrix

    private fun initialize(intent: Intent?): Boolean {
        if (intent == null) {
            Timber.d("## Sync: initialize intent is null")
            return false
        }
        val matrix = provideMatrix()
        val safeSessionId = intent.getStringExtra(EXTRA_SESSION_ID) ?: return false
        syncTimeoutSeconds = intent.getIntExtra(EXTRA_TIMEOUT_SECONDS, getDefaultSyncTimeoutSeconds())
        syncDelaySeconds = intent.getIntExtra(EXTRA_DELAY_SECONDS, getDefaultSyncDelaySeconds())
        try {
            val sessionComponent = matrix.sessionManager.getSessionComponent(safeSessionId)
                    ?: throw IllegalStateException("## Sync: You should have a session to make it work")
            session = sessionComponent.session()
            sessionId = safeSessionId
            syncTask = sessionComponent.syncTask()
            isInitialSync = !session.hasAlreadySynced()
            networkConnectivityChecker = sessionComponent.networkConnectivityChecker()
            taskExecutor = sessionComponent.taskExecutor()
            coroutineDispatchers = sessionComponent.coroutineDispatchers()
            backgroundDetectionObserver = matrix.backgroundDetectionObserver
            return true
        } catch (exception: Exception) {
            Timber.e(exception, "## Sync: An exception occurred during initialisation")
            return false
        }
    }

    abstract fun getDefaultSyncTimeoutSeconds(): Int

    abstract fun getDefaultSyncDelaySeconds(): Int

    abstract fun onStart(isInitialSync: Boolean)

    abstract fun onRescheduleAsked(sessionId: String, syncTimeoutSeconds: Int, syncDelaySeconds: Int)

    abstract fun onNetworkError(sessionId: String, syncTimeoutSeconds: Int, syncDelaySeconds: Int, isPeriodic: Boolean)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val EXTRA_SESSION_ID = "EXTRA_SESSION_ID"
        const val EXTRA_TIMEOUT_SECONDS = "EXTRA_TIMEOUT_SECONDS"
        const val EXTRA_DELAY_SECONDS = "EXTRA_DELAY_SECONDS"
        const val EXTRA_PERIODIC = "EXTRA_PERIODIC"
        const val EXTRA_NETWORK_BACK_RESTART = "EXTRA_NETWORK_BACK_RESTART"

        const val ACTION_STOP = "ACTION_STOP"
    }
}
