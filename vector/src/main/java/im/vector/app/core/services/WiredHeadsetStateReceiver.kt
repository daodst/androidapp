

package im.vector.app.core.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import timber.log.Timber
import java.lang.ref.WeakReference


class WiredHeadsetStateReceiver : BroadcastReceiver() {

    interface HeadsetEventListener {
        fun onHeadsetEvent(event: HeadsetPlugEvent)
    }

    var delegate: WeakReference<HeadsetEventListener>? = null

    data class HeadsetPlugEvent(
            val plugged: Boolean,
            val headsetName: String?,
            val hasMicrophone: Boolean
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        
        
        
        

        val isPlugged = when (intent?.getIntExtra("state", -1)) {
            0    -> false
            1    -> true
            else -> return Unit.also {
                Timber.v("## VOIP WiredHeadsetStateReceiver invalid state")
            }
        }
        val hasMicrophone = when (intent.getIntExtra("microphone", -1)) {
            1    -> true
            else -> false
        }

        delegate?.get()?.onHeadsetEvent(
                HeadsetPlugEvent(plugged = isPlugged, headsetName = intent.getStringExtra("name"), hasMicrophone = hasMicrophone)
        )
    }

    companion object {
        fun createAndRegister(context: Context, listener: HeadsetEventListener): WiredHeadsetStateReceiver {
            val receiver = WiredHeadsetStateReceiver()
            receiver.delegate = WeakReference(listener)
            val action = AudioManager.ACTION_HEADSET_PLUG
            context.registerReceiver(receiver, IntentFilter(action))
            return receiver
        }

        fun unRegister(context: Context, receiver: WiredHeadsetStateReceiver) {
            context.unregisterReceiver(receiver)
        }
    }
}
