

package im.vector.app.fdroid.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import im.vector.app.core.extensions.singletonEntryPoint
import im.vector.app.fdroid.BackgroundSyncStarter
import timber.log.Timber

class OnApplicationUpgradeOrRebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.v("## onReceive() ${intent.action}")
        val singletonEntryPoint = context.singletonEntryPoint()
        BackgroundSyncStarter.start(
                context,
                singletonEntryPoint.vectorPreferences(),
                singletonEntryPoint.activeSessionHolder()
        )
    }
}
