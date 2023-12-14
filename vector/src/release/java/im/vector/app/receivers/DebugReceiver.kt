

@file:Suppress("UNUSED_PARAMETER")

package im.vector.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter


class DebugReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        
    }

    companion object {
        fun getIntentFilter(context: Context) = IntentFilter()
    }
}
