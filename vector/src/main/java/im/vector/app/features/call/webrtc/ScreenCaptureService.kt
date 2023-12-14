

package im.vector.app.features.call.webrtc

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.services.VectorService
import im.vector.app.features.notifications.NotificationUtils
import javax.inject.Inject

@AndroidEntryPoint
class ScreenCaptureService : VectorService() {

    @Inject lateinit var notificationUtils: NotificationUtils
    private val binder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showStickyNotification()

        return START_STICKY
    }

    private fun showStickyNotification() {
        val notificationId = System.currentTimeMillis().toInt()
        val notification = notificationUtils.buildScreenSharingNotification()
        startForeground(notificationId, notification)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun stopService() {
        stopSelf()
    }

    inner class LocalBinder : Binder() {
        fun getService(): ScreenCaptureService = this@ScreenCaptureService
    }
}
