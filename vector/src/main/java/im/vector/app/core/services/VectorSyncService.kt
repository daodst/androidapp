
package im.vector.app.core.services

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.platform.PendingIntentCompat
import im.vector.app.features.notifications.NotificationUtils
import im.vector.app.features.settings.BackgroundSyncMode
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.session.sync.job.SyncService
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class VectorSyncService : SyncService() {

    companion object {

        fun newOneShotIntent(context: Context,
                             sessionId: String): Intent {
            return Intent(context, VectorSyncService::class.java).also {
                it.putExtra(EXTRA_SESSION_ID, sessionId)
                it.putExtra(EXTRA_TIMEOUT_SECONDS, 0)
                it.putExtra(EXTRA_PERIODIC, false)
            }
        }

        fun newPeriodicIntent(context: Context,
                              sessionId: String,
                              syncTimeoutSeconds: Int,
                              syncDelaySeconds: Int,
                              isNetworkBack: Boolean): Intent {
            return Intent(context, VectorSyncService::class.java).also {
                it.putExtra(EXTRA_SESSION_ID, sessionId)
                it.putExtra(EXTRA_TIMEOUT_SECONDS, syncTimeoutSeconds)
                it.putExtra(EXTRA_PERIODIC, true)
                it.putExtra(EXTRA_DELAY_SECONDS, syncDelaySeconds)
                it.putExtra(EXTRA_NETWORK_BACK_RESTART, isNetworkBack)
            }
        }

        fun stopIntent(context: Context): Intent {
            return Intent(context, VectorSyncService::class.java).also {
                it.action = ACTION_STOP
            }
        }
    }

    @Inject lateinit var notificationUtils: NotificationUtils
    @Inject lateinit var matrix: Matrix

    override fun provideMatrix() = matrix

    override fun getDefaultSyncDelaySeconds() = BackgroundSyncMode.DEFAULT_SYNC_DELAY_SECONDS

    override fun getDefaultSyncTimeoutSeconds() = BackgroundSyncMode.DEFAULT_SYNC_TIMEOUT_SECONDS

    override fun onStart(isInitialSync: Boolean) {
        val notificationSubtitleRes = if (isInitialSync) {
            R.string.notification_initial_sync
        } else {
            R.string.notification_listening_for_notifications2
        }
        val notification = notificationUtils.buildForegroundServiceNotification(notificationSubtitleRes, false)
        startForeground(NotificationUtils.NOTIFICATION_ID_FOREGROUND_SERVICE, notification)
    }

    override fun onRescheduleAsked(sessionId: String,
                                   syncTimeoutSeconds: Int,
                                   syncDelaySeconds: Int) {
        rescheduleSyncService(
                sessionId = sessionId,
                syncTimeoutSeconds = syncTimeoutSeconds,
                syncDelaySeconds = syncDelaySeconds,
                isPeriodic = true,
                isNetworkBack = false
        )
    }

    
    override fun onNetworkError(sessionId: String,
                                syncTimeoutSeconds: Int,
                                syncDelaySeconds: Int,
                                isPeriodic: Boolean) {
        Timber.d("## Sync: A network error occurred during sync")
        val rescheduleSyncWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<RestartWhenNetworkOn>()
                        .setInputData(RestartWhenNetworkOn.createInputData(sessionId, syncTimeoutSeconds, syncDelaySeconds, isPeriodic))
                        .setConstraints(
                                Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build()
                        )
                        .build()

        Timber.d("## Sync: Schedule a work to restart service when network will be on")
        WorkManager
                .getInstance(applicationContext)
                .enqueue(rescheduleSyncWorkRequest)
    }

    override fun onDestroy() {
        removeForegroundNotification()
        super.onDestroy()
    }

    private fun removeForegroundNotification() {
        val notificationManager = getSystemService<NotificationManager>()!!
        notificationManager.cancel(NotificationUtils.NOTIFICATION_ID_FOREGROUND_SERVICE)
    }

    
    class RestartWhenNetworkOn(appContext: Context, workerParams: WorkerParameters) :
            Worker(appContext, workerParams) {
        override fun doWork(): Result {
            Timber.d("## Sync: RestartWhenNetworkOn.doWork()")
            val sessionId = inputData.getString(KEY_SESSION_ID) ?: return Result.failure()
            val syncTimeoutSeconds = inputData.getInt(KEY_SYNC_TIMEOUT_SECONDS, BackgroundSyncMode.DEFAULT_SYNC_TIMEOUT_SECONDS)
            val syncDelaySeconds = inputData.getInt(KEY_SYNC_DELAY_SECONDS, BackgroundSyncMode.DEFAULT_SYNC_DELAY_SECONDS)
            val isPeriodic = inputData.getBoolean(KEY_IS_PERIODIC, false)
            applicationContext.rescheduleSyncService(
                    sessionId = sessionId,
                    syncTimeoutSeconds = syncTimeoutSeconds,
                    syncDelaySeconds = syncDelaySeconds,
                    isPeriodic = isPeriodic,
                    isNetworkBack = true
            )
            
            return Result.success()
        }

        companion object {
            fun createInputData(sessionId: String,
                                syncTimeoutSeconds: Int,
                                syncDelaySeconds: Int,
                                isPeriodic: Boolean
            ): Data {
                return Data.Builder()
                        .putString(KEY_SESSION_ID, sessionId)
                        .putInt(KEY_SYNC_TIMEOUT_SECONDS, syncTimeoutSeconds)
                        .putInt(KEY_SYNC_DELAY_SECONDS, syncDelaySeconds)
                        .putBoolean(KEY_IS_PERIODIC, isPeriodic)
                        .build()
            }

            private const val KEY_SESSION_ID = "sessionId"
            private const val KEY_SYNC_TIMEOUT_SECONDS = "timeout"
            private const val KEY_SYNC_DELAY_SECONDS = "delay"
            private const val KEY_IS_PERIODIC = "isPeriodic"
        }
    }
}

private fun Context.rescheduleSyncService(sessionId: String,
                                          syncTimeoutSeconds: Int,
                                          syncDelaySeconds: Int,
                                          isPeriodic: Boolean,
                                          isNetworkBack: Boolean) {
    Timber.d("## Sync: rescheduleSyncService")
    val intent = if (isPeriodic) {
        VectorSyncService.newPeriodicIntent(
                context = this,
                sessionId = sessionId,
                syncTimeoutSeconds = syncTimeoutSeconds,
                syncDelaySeconds = syncDelaySeconds,
                isNetworkBack = isNetworkBack
        )
    } else {
        VectorSyncService.newOneShotIntent(
                context = this,
                sessionId = sessionId
        )
    }

    if (isNetworkBack || syncDelaySeconds == 0) {
        
        startService(intent)
    } else {
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(this, 0, intent, PendingIntentCompat.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getService(this, 0, intent, PendingIntentCompat.FLAG_IMMUTABLE)
        }
        val firstMillis = System.currentTimeMillis() + syncDelaySeconds * 1000L
        val alarmMgr = getSystemService<AlarmManager>()!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstMillis, pendingIntent)
        } else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, firstMillis, pendingIntent)
        }
    }
}
