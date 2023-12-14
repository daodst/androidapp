

package im.vector.app.fdroid

import android.content.Context
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.fdroid.receiver.AlarmSyncBroadcastReceiver
import im.vector.app.features.settings.BackgroundSyncMode
import im.vector.app.features.settings.VectorPreferences
import timber.log.Timber

object BackgroundSyncStarter {
    fun start(context: Context, vectorPreferences: VectorPreferences, activeSessionHolder: ActiveSessionHolder) {
        if (vectorPreferences.areNotificationEnabledForDevice()) {
            val activeSession = activeSessionHolder.getSafeActiveSession() ?: return
            when (vectorPreferences.getFdroidSyncBackgroundMode()) {
                BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_BATTERY  -> {
                    
                    Timber.i("## Sync: Work scheduled to periodically sync in ${vectorPreferences.backgroundSyncDelay()}s")
                    activeSession.startAutomaticBackgroundSync(
                            vectorPreferences.backgroundSyncTimeOut().toLong(),
                            vectorPreferences.backgroundSyncDelay().toLong()
                    )
                }
                BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_REALTIME -> {
                    
                    AlarmSyncBroadcastReceiver.scheduleAlarm(context, activeSession.sessionId, vectorPreferences.backgroundSyncDelay())
                    Timber.i("## Sync: Alarm scheduled to start syncing")
                }
                BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_DISABLED     -> {
                    
                    Timber.i("## Sync: background sync is disabled")
                }
            }
        }
    }
}
