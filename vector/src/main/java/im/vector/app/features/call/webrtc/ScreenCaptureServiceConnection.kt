

package im.vector.app.features.call.webrtc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import javax.inject.Inject

class ScreenCaptureServiceConnection @Inject constructor(
        private val context: Context
) : ServiceConnection {

    private var isBound = false
    private var screenCaptureService: ScreenCaptureService? = null

    fun bind() {
        if (!isBound) {
            Intent(context, ScreenCaptureService::class.java).also { intent ->
                context.bindService(intent, this, 0)
            }
        }
    }

    fun stopScreenCapturing() {
        screenCaptureService?.stopService()
    }

    override fun onServiceConnected(className: ComponentName, binder: IBinder) {
        screenCaptureService = (binder as ScreenCaptureService.LocalBinder).getService()
        isBound = true
    }

    override fun onServiceDisconnected(className: ComponentName) {
        isBound = false
        screenCaptureService = null
    }
}
