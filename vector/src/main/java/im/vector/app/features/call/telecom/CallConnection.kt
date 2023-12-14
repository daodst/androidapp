

package im.vector.app.features.call.telecom

import android.content.Context
import android.os.Build
import android.telecom.Connection
import android.telecom.DisconnectCause
import androidx.annotation.RequiresApi
import im.vector.app.features.call.webrtc.WebRtcCallManager
import timber.log.Timber
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M) class CallConnection(
        private val context: Context,
        private val roomId: String,
        val callId: String
) : Connection() {

    @Inject lateinit var callManager: WebRtcCallManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            connectionProperties = PROPERTY_SELF_MANAGED
        }
    }

    
    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        Timber.i("onShowIncomingCallUi")
        
    }

    override fun onAnswer() {
        super.onAnswer()
        
        Timber.i("onShowIncomingCallUi")
    }

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        Timber.i("onStateChanged${stateToString(state)}")
    }

    override fun onReject() {
        super.onReject()
        Timber.i("onReject")
        close()
    }

    override fun onDisconnect() {
        onDisconnect()
        Timber.i("onDisconnect")
        close()
    }

    private fun close() {
        setDisconnected(DisconnectCause(DisconnectCause.CANCELED))
        destroy()
    }

    private fun startCall() {
        
    }
}
