
@file:Suppress("UNUSED_PARAMETER")

package im.vector.app.push.fcm

import android.app.Activity
import android.content.Context
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.pushers.PushersManager
import im.vector.app.fdroid.BackgroundSyncStarter
import im.vector.app.fdroid.receiver.AlarmSyncBroadcastReceiver
import im.vector.app.features.settings.VectorPreferences


object FcmHelper {

    fun isPushSupported(): Boolean = false

    
    fun getFcmToken(context: Context): String? {
        return null
    }

    
    fun storeFcmToken(context: Context, token: String?) {
        
    }

    
    fun ensureFcmTokenIsRetrieved(activity: Activity, pushersManager: PushersManager, registerPusher: Boolean) {
        
    }

    fun onEnterForeground(context: Context, activeSessionHolder: ActiveSessionHolder) {
        
        activeSessionHolder.getSafeActiveSession()?.stopAnyBackgroundSync()
        AlarmSyncBroadcastReceiver.cancelAlarm(context)
    }

    fun onEnterBackground(context: Context, vectorPreferences: VectorPreferences, activeSessionHolder: ActiveSessionHolder) {
        BackgroundSyncStarter.start(context, vectorPreferences, activeSessionHolder)
    }
}
