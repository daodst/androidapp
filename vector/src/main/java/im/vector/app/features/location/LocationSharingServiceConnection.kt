

package im.vector.app.features.location

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import javax.inject.Inject

class LocationSharingServiceConnection @Inject constructor(
        private val context: Context
) : ServiceConnection {

    interface Callback {
        fun onLocationServiceRunning()
        fun onLocationServiceStopped()
    }

    private var callback: Callback? = null
    private var isBound = false
    private var locationSharingService: LocationSharingService? = null

    fun bind(callback: Callback) {
        this.callback = callback

        if (isBound) {
            callback.onLocationServiceRunning()
        } else {
            Intent(context, LocationSharingService::class.java).also { intent ->
                context.bindService(intent, this, 0)
            }
        }
    }

    fun unbind() {
        callback = null
    }

    fun stopLiveLocationSharing(roomId: String) {
        locationSharingService?.stopSharingLocation(roomId)
    }

    override fun onServiceConnected(className: ComponentName, binder: IBinder) {
        locationSharingService = (binder as LocationSharingService.LocalBinder).getService()
        isBound = true
        callback?.onLocationServiceRunning()
    }

    override fun onServiceDisconnected(className: ComponentName) {
        isBound = false
        locationSharingService = null
        callback?.onLocationServiceStopped()
    }
}
