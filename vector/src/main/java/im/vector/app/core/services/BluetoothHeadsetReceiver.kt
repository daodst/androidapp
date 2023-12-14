

package im.vector.app.core.services

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import java.lang.ref.WeakReference

class BluetoothHeadsetReceiver : BroadcastReceiver() {

    interface EventListener {
        fun onBTHeadsetEvent(event: BTHeadsetPlugEvent)
    }

    var delegate: WeakReference<EventListener>? = null

    data class BTHeadsetPlugEvent(
            val plugged: Boolean,
            val headsetName: String?,
            
            val deviceClass: Int
    )

    override fun onReceive(context: Context?, intent: Intent?) {
        
        
        
        
        
        

        val headsetConnected = when (intent?.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, -1)) {
            BluetoothAdapter.STATE_CONNECTED    -> true
            BluetoothAdapter.STATE_DISCONNECTED -> false
            else                                -> return 
        }

        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        val deviceName = device?.name
        when (device?.bluetoothClass?.deviceClass) {
            BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE,
            BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO,
            BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET -> {
                
                delegate?.get()?.onBTHeadsetEvent(
                        BTHeadsetPlugEvent(
                                plugged = headsetConnected,
                                headsetName = deviceName,
                                deviceClass = device.bluetoothClass.deviceClass
                        )
                )
            }
            else                                               -> return
        }
    }

    companion object {
        fun createAndRegister(context: Context, listener: EventListener): BluetoothHeadsetReceiver {
            val receiver = BluetoothHeadsetReceiver()
            receiver.delegate = WeakReference(listener)
            context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED))
            return receiver
        }

        fun unRegister(context: Context, receiver: BluetoothHeadsetReceiver) {
            context.unregisterReceiver(receiver)
        }
    }
}
