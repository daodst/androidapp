

package org.matrix.android.sdk.internal.session.sync.job

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.isTokenError
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.call.MxCall
import org.matrix.android.sdk.api.session.sync.SyncState
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.internal.network.NetworkConnectivityChecker
import org.matrix.android.sdk.internal.session.call.ActiveCallHandler
import org.matrix.android.sdk.internal.session.sync.SyncTask
import org.matrix.android.sdk.internal.session.sync.job.ws.IAction
import org.matrix.android.sdk.internal.session.sync.job.ws.SocketIoManager
import org.matrix.android.sdk.internal.settings.DefaultLightweightSettingsStorage
import org.matrix.android.sdk.internal.util.BackgroundDetectionObserver
import org.matrix.android.sdk.internal.util.Debouncer
import org.matrix.android.sdk.internal.util.createUIHandler
import timber.log.Timber
import java.net.SocketTimeoutException
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject
import kotlin.concurrent.schedule

private const val RETRY_WAIT_TIME_MS = 10_000L
private const val DEFAULT_LONG_POOL_TIMEOUT = 30_000L

private val loggerTag = LoggerTag("SyncThread", LoggerTag.SYNC)

internal class SyncThread @Inject constructor(private val ioManager: SocketIoManager, private val syncTask: SyncTask, private val networkConnectivityChecker: NetworkConnectivityChecker, private val backgroundDetectionObserver: BackgroundDetectionObserver, private val activeCallHandler: ActiveCallHandler, private val lightweightSettingsStorage: DefaultLightweightSettingsStorage) : Thread(
        "SyncThread"
), NetworkConnectivityChecker.Listener, BackgroundDetectionObserver.Listener {

    private var state: SyncState = SyncState.Idle
    private var liveState = MutableLiveData(state)
    private val lock = Object()
    private val syncScope = CoroutineScope(SupervisorJob())
    private val debouncer = Debouncer(createUIHandler())

    private var canReachServer = true
    private var isStarted = false
    private var isTokenValid = true
    private var retryNoNetworkTask: TimerTask? = null
    private var previousSyncResponseHasToDevice = false

    private val activeCallListObserver = Observer<MutableList<MxCall>> { activeCalls ->
        if (activeCalls.isEmpty() && backgroundDetectionObserver.isInBackground) {
            pause()
        }
    }

    private val _syncFlow = MutableSharedFlow<SyncResponse>()

    init {
        updateStateTo(SyncState.Idle)
    }

    fun setInitialForeground(initialForeground: Boolean) {
        val newState = if (initialForeground) SyncState.Idle else SyncState.Paused
        updateStateTo(newState)
    }

    fun restart() = synchronized(lock) {
        if (!isStarted) {
            Timber.tag(loggerTag.value).d("Resume sync...")
            isStarted = true
            
            canReachServer = true
            isTokenValid = true
            lock.notify()
        }
    }

    fun pause() = synchronized(lock) {
        if (isStarted) {
            Timber.tag(loggerTag.value).d("Pause sync... Not cancelling incremental sync")
            isStarted = false
            retryNoNetworkTask?.cancel()
            
            
            
        }
    }

    fun kill() = synchronized(lock) {
        Timber.tag(loggerTag.value).d("Kill sync...")
        updateStateTo(SyncState.Killing)
        retryNoNetworkTask?.cancel()
        syncScope.coroutineContext.cancelChildren()
        lock.notify()
        ioManager.stop()
    }

    fun currentState() = state

    fun liveState(): LiveData<SyncState> {
        return liveState
    }

    fun syncFlow(): SharedFlow<SyncResponse> = _syncFlow

    override fun onConnectivityChanged() {
        retryNoNetworkTask?.cancel()
        synchronized(lock) {
            canReachServer = true
            lock.notify()
        }
    }

    override fun run() {
        Timber.tag(loggerTag.value).d("Start syncing...")

        isStarted = true
        networkConnectivityChecker.register(this)
        backgroundDetectionObserver.register(this)
        registerActiveCallsObserver()
        while (state != SyncState.Killing) {
            if (false) {
                doSync()
            } else {
                Timber.i("---onConnect------？----${canReachServer}--------------")
                if (!canReachServer) {
                    updateState()
                    synchronized(lock) {
                        lock.wait()
                    }
                } else {
                    
                    val sync = syncScope.launch {
                        delay(3_000)
                    }
                    
                    runBlocking {
                        sync.join()
                    }
                }
                Timber.i("---onConnect-----------${canReachServer}--------------")
                startIOManager()
            }
        }
        Timber.tag(loggerTag.value).d("Sync killed")
        updateStateTo(SyncState.Killed)
        backgroundDetectionObserver.unregister(this)
        networkConnectivityChecker.unregister(this)
        unregisterActiveCallsObserver()
    }

    var onceLoop = false;
    private fun startIOManager() {
        ioManager.start(object : IAction {
            override fun action(): Boolean {
                doFetch()
                return onceLoop
            }
        })
    }



    private fun updateState() {
        
        onceLoop = false
        Timber.tag(loggerTag.value).d("Entering loop, state: $state")
        if (!isStarted) {
            Timber.tag(loggerTag.value).d("Sync is Paused. Waiting...")
            updateStateTo(SyncState.Paused)
            Timber.tag(loggerTag.value).d("...unlocked")
        } else if (!canReachServer) {
            Timber.tag(loggerTag.value).d("No network. Waiting...")
            updateStateTo(SyncState.NoNetwork)
            
            Timber.tag(loggerTag.value).d("...retry")
        } else if (!isTokenValid) {
            if (state == SyncState.Killing) {
                return
            }
            Timber.tag(loggerTag.value).d("Token is invalid. Waiting...")
            updateStateTo(SyncState.InvalidToken)
            Timber.tag(loggerTag.value).d("...unlocked")
        }
    }

    private fun doSync() {
        
        onceLoop = false
        Timber.tag(loggerTag.value).d("Entering loop, state: $state")
        if (!isStarted) {
            Timber.tag(loggerTag.value).d("Sync is Paused. Waiting...")
            updateStateTo(SyncState.Paused)
            synchronized(lock) { lock.wait() }
            Timber.tag(loggerTag.value).d("...unlocked")
        } else if (!canReachServer) {
            Timber.tag(loggerTag.value).d("No network. Waiting...")
            updateStateTo(SyncState.NoNetwork)
            
            retryNoNetworkTask = Timer(SyncState.NoNetwork.toString(), false).schedule(RETRY_WAIT_TIME_MS) {
                synchronized(lock) {
                    canReachServer = true
                    lock.notify()
                }
            }
            synchronized(lock) { lock.wait() }
            Timber.tag(loggerTag.value).d("...retry")
        } else if (!isTokenValid) {
            if (state == SyncState.Killing) {
                return
            }
            Timber.tag(loggerTag.value).d("Token is invalid. Waiting...")
            updateStateTo(SyncState.InvalidToken)
            synchronized(lock) { lock.wait() }
            Timber.tag(loggerTag.value).d("...unlocked")
        } else {
            doFetch()
        }
    }

    private fun doFetch() {
        if (state !is SyncState.Running) {
            updateStateTo(SyncState.Running(afterPause = true))
        }
        val afterPause = state.let { it is SyncState.Running && it.afterPause }
        val timeout = when {
            previousSyncResponseHasToDevice -> 0L 
            afterPause                      -> 0L 
            else                            -> DEFAULT_LONG_POOL_TIMEOUT
        }
        Timber.tag(loggerTag.value).d("Execute sync request with timeout $timeout")
        val presence = lightweightSettingsStorage.getSyncPresenceStatus()
        val params = SyncTask.Params(timeout, presence, afterPause = afterPause)
        val sync = syncScope.launch {
            previousSyncResponseHasToDevice = doSync(params)
        }
        runBlocking {
            sync.join()
        }
        Timber.tag(loggerTag.value).d("...Continue")
    }

    private fun registerActiveCallsObserver() {
        syncScope.launch(Dispatchers.Main) {
            activeCallHandler.getActiveCallsLiveData().observeForever(activeCallListObserver)
        }
    }

    private fun unregisterActiveCallsObserver() {
        syncScope.launch(Dispatchers.Main) {
            activeCallHandler.getActiveCallsLiveData().removeObserver(activeCallListObserver)
        }
    }

    
    private suspend fun doSync(params: SyncTask.Params): Boolean {
        return try {
            val syncResponse = syncTask.execute(params)
            _syncFlow.emit(syncResponse)
            onceLoop = true
            syncResponse.toDevice?.events?.isNotEmpty().orFalse()
        } catch (failure: Throwable) {
            if (failure is Failure.NetworkConnection) {
                canReachServer = false
            }
            if (failure is Failure.NetworkConnection && failure.cause is SocketTimeoutException) {
                
                Timber.tag(loggerTag.value).d("Timeout")
            } else if (failure is CancellationException) {
                Timber.tag(loggerTag.value).d("Cancelled")
            } else if (failure.isTokenError()) {
                
                Timber.tag(loggerTag.value).w(failure, "Token error")
                isStarted = false
                isTokenValid = false
            } else {
                Timber.tag(loggerTag.value).e(failure)
                if (failure !is Failure.NetworkConnection || failure.cause is JsonEncodingException) {
                    
                    Timber.tag(loggerTag.value).d("Wait 10s")
                    delay(RETRY_WAIT_TIME_MS)
                }
            }
            false
        } finally {
            state.let {
                if (it is SyncState.Running && it.afterPause) {
                    updateStateTo(SyncState.Running(afterPause = false))
                }
            }
        }
    }

    private fun updateStateTo(newState: SyncState) {
        Timber.tag(loggerTag.value).d("Update state from $state to $newState")
        if (newState == state) {
            return
        }
        state = newState
        debouncer.debounce("post_state", {
            liveState.value = newState
        }, 150)
    }

    override fun onMoveToForeground() {
        restart()
    }

    override fun onMoveToBackground() {
        if (activeCallHandler.getActiveCallsLiveData().value.isNullOrEmpty()) {
            pause()
        }
    }
}
