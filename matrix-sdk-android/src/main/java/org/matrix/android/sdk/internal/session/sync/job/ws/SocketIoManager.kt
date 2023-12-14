package org.matrix.android.sdk.internal.session.sync.job.ws

import android.text.TextUtils
import com.squareup.moshi.Moshi
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.Transport
import io.socket.engineio.client.transports.PollingXHR
import io.socket.engineio.client.transports.WebSocket
import io.socket.engineio.parser.Parser
import io.socket.thread.EventThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.api.session.sync.model.SyncResponse
import org.matrix.android.sdk.api.session.utils.model.UtilsRpcUrl
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.token.HomeserverAccessTokenProvider
import org.matrix.android.sdk.internal.session.sync.SyncResponseHandler
import org.matrix.android.sdk.internal.session.sync.SyncTokenStore
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.AUTH_RES
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.AccountData
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_ACCOUNT_DATA
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_KEY_CHANGE
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_NEW_EVENT
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_NEW_INVITE
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_NOTIFICATION_DATA
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_PRESENCE
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_RECEIPT
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_RETIRE_NEW_INVITE
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_SEND_TO_DEVICE
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.CMD_TYPING
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.KeyChange
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.NewEvent
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.NewInvite
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.NotificationData
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.Presence
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.Receipt
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.RetireNewInvite
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.SendToDevice
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.StreamingToken
import org.matrix.android.sdk.internal.session.sync.job.ws.pojo.Typing
import timber.log.Timber
import java.net.URISyntaxException
import java.util.logging.ConsoleHandler
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger
import javax.inject.Inject

internal class SocketIoManager @Inject constructor(private val globalErrorReceiver: GlobalErrorReceiver, private val moshi: Moshi, private val syncTokenStore: SyncTokenStore, private val syncResponseHandler: SyncResponseHandler, private val accessTokenProvider: HomeserverAccessTokenProvider) {

    var onceLoop = false;

    var onConnectFlag = false
    var onConnecTingFlag = false

    val lockSocket = Object()
    private val syncScope = CoroutineScope(SupervisorJob())

    
    var mSocket: Socket? = null





    private fun initSocket() {
        try {
            changeLoggerLevel()

            val url = "http://${UtilsRpcUrl.getWsUrl()}"
            
            val opts = IO.Options()
            opts.forceNew = true
            opts.transports = arrayOf(WebSocket.NAME)
            opts.path = "/_matrix/client/r0/socket.io/"
            mSocket = IO.socket(url, opts)

            
            mSocket?.on(Socket.EVENT_CONNECT, onConnect)
            mSocket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket?.on(Socket.EVENT_ERROR, onConnectError)
            mSocket?.on("msg", onMsg)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    var mAction: IAction? = null

    
    fun start(action: IAction) {
        Timber.i("---------onConnect--------${System.currentTimeMillis()}----------start-----${this}--------${onConnectFlag}-----------$onConnecTingFlag-----")
        mAction = action
        if (!onConnectFlag && !onConnecTingFlag) {
            onConnecTingFlag = true
            initSocket()
            Timber.i("---------onConnect--------${System.currentTimeMillis()}----------start123456------${mSocket}-------")
            mSocket?.open()
            
            synchronized(lockSocket) { lockSocket.wait() }
            Timber.i("---------onConnect--------${System.currentTimeMillis()}----------start123456------？-------")
        }
    }

    fun stop() {
        
        mSocket?.disconnect()
        
        mSocket?.off(Socket.EVENT_CONNECT)
        mSocket?.off(Socket.EVENT_DISCONNECT)
        mSocket?.off(Socket.EVENT_CONNECT_ERROR)
        mSocket?.off(Socket.EVENT_ERROR)
        mSocket?.off("msg")
    }

    
    private val onMsg = Emitter.Listener { args ->

        val data = args[0] as String

        val seq = try {
            JSONObject(data).getInt("seq")
        } catch (e: JSONException) {
            -1
        }

        val cmd = try {
            JSONObject(data).getString("cmd")
        } catch (e: JSONException) {
            ""
        }
        println("----onConnect----onMsg------$cmd-------111111111--------${onConnectFlag}--${onConnecTingFlag}--")
        if (!onConnectFlag) {
            val body = try {
                JSONObject(data).getString("body")
            } catch (e: JSONException) {
                ""
            }
            if (AUTH_RES.equals(cmd) && TextUtils.equals(body, "ok")) {
                mSeq = seq
                onConnectFlag = true
                onConnecTingFlag = false
                while (!onceLoop) {
                    
                    onceLoop = mAction?.action() == true
                }
            } else {
                
                globalErrorReceiver.handleGlobalError(GlobalError.InvalidToken(true))
            }
        } else {
            if (mSeq + 1 == seq) {
                mSeq = seq
                doWsSync(cmd, data)
            } else {
                
                stop()
                
                onConnecTingFlag = false
                onceLoop = false
                onConnectFlag = false
                synchronized(lockSocket) { lockSocket.notifyAll() }
            }
        }
    }

    var mSeq: Int = 0

    private fun doWsSync(cmd: String, data: String) {

        
        val response: SyncResponse? = if (CMD_TYPING == cmd) {
            
            Typing.getSyncResponse(moshi, data)
        } else if (CMD_PRESENCE == cmd) {
            
            Presence.getSyncResponse(moshi, data)
        } else if (CMD_ACCOUNT_DATA == cmd) {
            
            AccountData.getSyncResponse(moshi, data)
        } else if (CMD_KEY_CHANGE == cmd) {
            
            KeyChange.getSyncResponse(moshi, data)
        } else if (CMD_RECEIPT == cmd) {
            
            Receipt.getSyncResponse(moshi, data)
        } else if (CMD_NOTIFICATION_DATA == cmd) {
            
            NotificationData.getSyncResponse(moshi, data)
        } else if (CMD_SEND_TO_DEVICE == cmd) {
            
            SendToDevice.getSyncResponse(moshi, data)
        } else if (CMD_NEW_EVENT == cmd) {
            
            NewEvent.getSyncResponse(moshi, data)
        } else if (CMD_NEW_INVITE == cmd) {
            
            NewInvite.getSyncResponse(moshi, data)
        } else if (CMD_RETIRE_NEW_INVITE == cmd) {
            RetireNewInvite.getSyncResponse(moshi, data)
        } else {
            null
        }

        response?.takeIf {
            
            it.canUse
        }?.let {
            
            val nextBatch = updateSince(cmd, it.position ?: 0)
            it.copy(nextBatch = nextBatch)
        }?.also {
            
            val token = syncTokenStore.getLastToken()

            if (false) {
                
                val adapter = moshi.adapter(SyncResponse::class.java)
                Timber.i("------SyncThread---toJson--------${adapter.toJson(it)}-----")
            }
            
            val sync = syncScope.launch {
                
                syncResponseHandler.handleResponse(it, token, null, onceLoop)
            }
            runBlocking {
                sync.join()
            }
        }
    }

    private fun updateSince(cmd: String, position: Int): String? {

        return onceLoop.takeIf { it }?.let {
            syncTokenStore
                    .getLastToken()
                    ?.drop(1)
                    ?.split("_")
                    ?.map {
                        it.toInt()
                    }
                    ?.let {
                        val curSince = StreamingToken(0, 0, 0, 0, 0, 0, 0, 0, 0)
                        it.forEachIndexed { index, value ->
                            if (index == 0) {
                                curSince.pduPosition = value;
                            }
                            if (index == 1) {
                                curSince.typingPosition = value;
                            }
                            if (index == 2) {
                                curSince.receiptPosition = value;
                            }
                            if (index == 3) {
                                curSince.sendToDevicePosition = value;
                            }
                            if (index == 4) {
                                curSince.invitePosition = value;
                            }
                            if (index == 5) {
                                curSince.accountDataPosition = value;
                            }
                            if (index == 6) {
                                curSince.deviceListPosition = value;
                            }
                            if (index == 7) {
                                curSince.notificationDataPosition = value;
                            }
                            if (index == 8) {
                                curSince.presencePosition = value;
                            }
                        }

                        if (CMD_TYPING.equals(cmd) && position > curSince.typingPosition) {
                            curSince.typingPosition = position
                        } else if (CMD_PRESENCE.equals(cmd) && position > curSince.presencePosition) {
                            curSince.presencePosition = position;
                        } else if (CMD_ACCOUNT_DATA.equals(cmd) && position > curSince.accountDataPosition) {
                            curSince.accountDataPosition = position;
                        } else if (CMD_KEY_CHANGE.equals(cmd) && position > curSince.deviceListPosition) {
                            curSince.deviceListPosition = position;
                        } else if (CMD_RECEIPT.equals(cmd) && position > curSince.receiptPosition) {
                            curSince.receiptPosition = position;
                        } else if (CMD_NOTIFICATION_DATA.equals(cmd) && position > curSince.notificationDataPosition) {
                            curSince.notificationDataPosition = position;
                        } else if (CMD_SEND_TO_DEVICE.equals(cmd) && position > curSince.sendToDevicePosition) {
                            curSince.sendToDevicePosition = position;
                        } else if (CMD_NEW_EVENT.equals(cmd) && position > curSince.pduPosition) {
                            curSince.pduPosition = position;
                        } else if (CMD_NEW_INVITE.equals(cmd) && position > curSince.invitePosition) {
                            curSince.invitePosition = position;
                        } else if (CMD_RETIRE_NEW_INVITE.equals(cmd) && position > curSince.invitePosition) {
                            curSince.invitePosition = position;
                        }
                        return curSince.toString()
                    }
        }
    }

    private val onConnect = Emitter.Listener {
        val token = accessTokenProvider.getToken();
        val obj = JSONObject()
        obj.put("cmd", "AUTH")
        obj.put("body", token)
        mSocket?.emit("msg", obj.toString())
        Timber.i("=======onConnect=====$obj=====SyncThread====${System.currentTimeMillis()}=======$token==")
    }

    private val onDisconnect = Emitter.Listener {
        Timber.i("=======onDisconnect=====onConnect=====SyncThread=====${System.currentTimeMillis()}========")
        
        mAction?.action()

        onConnecTingFlag = false
        onceLoop = false
        onConnectFlag = false
        stop()
        synchronized(lockSocket) { lockSocket.notifyAll() }
        Timber.i("---------onConnect--------${System.currentTimeMillis()}----------start123456-------------")

    }

    
    private val onConnectError = Emitter.Listener { args ->
        if (true) {
            Timber.i("=======onConnectError=====onConnect=====SyncThread====${System.currentTimeMillis()}=========")
            args?.let {
                it.forEach {
                    Timber.i("==onConnectError===onConnect======$it============")
                }

            }
        }
        
        
        mAction?.action()
        
        onConnecTingFlag = false
        onceLoop = false
        onConnectFlag = false

        stop()
        synchronized(lockSocket) { lockSocket.notifyAll() }
        Timber.i("---------onConnect--------${System.currentTimeMillis()}----------start123456-------------")
    }

    private fun changeLoggerLevel() {
        
        val logger: Logger = Logger.getLogger(Socket::class.java.name)
        logger.setLevel(Level.FINE)
        val consoleHandler: Handler = ConsoleHandler()
        consoleHandler.setLevel(Level.ALL)
        logger.addHandler(consoleHandler)
        val loggerName = ArrayList<String>()
        val i = IO.protocol
        val j = Socket.EVENT_CONNECT
        val k = Manager.EVENT_CLOSE
        try {
            val manager = Manager::class.java.newInstance()
            val webSocket = WebSocket(IO.Options())
            EventThread.exec { }
            val pollingXHR = PollingXHR(
                    Transport.Options()
            )
            val socket = io.socket.engineio.client.Socket()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        val m = PollingXHR.EVENT_REQUEST_HEADERS
        val n = WebSocket.EVENT_DRAIN
        loggerName.add(IO::class.java.name)
        loggerName.add(Socket::class.java.name)
        loggerName.add(Manager::class.java.name)
        loggerName.add(PollingXHR::class.java.name)
        loggerName.add(WebSocket::class.java.name)
        loggerName.add(io.socket.engineio.client.Socket::class.java.name)
        loggerName.add(Parser::class.java.name)
        for (tag in loggerName) {
            try {
                val temp: Logger? = LogManager.getLogManager().getLogger(tag)
                temp?.level = Level.ALL
                temp?.addHandler(consoleHandler)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
