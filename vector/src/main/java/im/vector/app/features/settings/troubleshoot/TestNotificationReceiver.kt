

package im.vector.app.features.settings.troubleshoot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class TestNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}
