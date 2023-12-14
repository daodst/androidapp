

package im.vector.app.features.call.telecom

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.PhoneAccountHandle
import android.telecom.StatusHints
import android.telecom.TelecomManager
import androidx.annotation.RequiresApi
import im.vector.app.core.services.CallService


@RequiresApi(Build.VERSION_CODES.M) class VectorConnectionService : ConnectionService() {

    
    override fun onCreateOutgoingConnection(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?): Connection? {
        val callId = request?.address?.encodedQuery ?: return null
        val roomId = request.extras.getString("MX_CALL_ROOM_ID") ?: return null
        return CallConnection(applicationContext, roomId, callId)
    }

    override fun onCreateIncomingConnection(connectionManagerPhoneAccount: PhoneAccountHandle?, request: ConnectionRequest?): Connection {
        val roomId = request?.extras?.getString("MX_CALL_ROOM_ID") ?: return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request)
        val callId = request.extras.getString("MX_CALL_CALL_ID") ?: return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request)

        val connection = CallConnection(applicationContext, roomId, callId)
        connection.connectionCapabilities = Connection.CAPABILITY_MUTE
        connection.audioModeIsVoip = true
        connection.setAddress(Uri.fromParts("tel", "+905000000000", null), TelecomManager.PRESENTATION_ALLOWED)
        connection.setCallerDisplayName("Element Caller", TelecomManager.PRESENTATION_ALLOWED)
        connection.statusHints = StatusHints("Testing Hint...", null, null)

        bindService(Intent(applicationContext, CallService::class.java), CallServiceConnection(connection), 0)
        connection.setInitializing()
        return connection
    }

    inner class CallServiceConnection(private val callConnection: CallConnection) : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val callSrvBinder = binder as CallService.CallServiceBinder
            callSrvBinder.getCallService().addConnection(callConnection)
            unbindService(this)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    companion object {
        const val TAG = "TComService"
    }
}
