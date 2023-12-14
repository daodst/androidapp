package com.wallet.ctc.vpn.service

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.os.ParcelFileDescriptor
import chat_sdk.PacketFlow
import com.beust.klaxon.Klaxon
import com.wallet.IChatInfo
import com.wallet.ctc.R
import com.wallet.ctc.crypto.WalletUtil
import com.wallet.ctc.vpn.Constants
import common.app.utils.AllUtils.urlToHost
import common.app.utils.SpUtil.getDefNode
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.concurrent.thread
import chat_sdk.Chat_sdk as Tun2socks
import chat_sdk.VpnService as Tun2socksVpnService

open class SimpleVpnService : VpnService() {

    private var configString: String = ""
    private var pfd: ParcelFileDescriptor? = null
    private var inputStream: FileInputStream? = null
    private var outputStream: FileOutputStream? = null
    private var buffer = ByteBuffer.allocate(1501)

    @Volatile private var running = false
    private lateinit var bgThread: Thread

    private val cm by lazy { this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    @TargetApi(28) private var underlyingNetwork: Network? = null
        @TargetApi(28) set(value) {
            setUnderlyingNetworks(if (value == null) null else arrayOf(value))
            field = value
        }

    companion object {
        @TargetApi(28) private val defaultNetworkRequest =
                NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED).build()
    }

    @TargetApi(28) private val defaultNetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            underlyingNetwork = network
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            underlyingNetwork = network
        }

        override fun onLost(network: Network) {
            underlyingNetwork = null
        }
    }

    data class Config(val outbounds: List<Outbound>? = null, val outboundDetour: List<Outbound>? = null, val outbound: Outbound? = null, val dns: Dns? = null)

    data class Dns(val servers: List<Any>? = null)
    data class Outbound(val protocol: String = "", val settings: Settings? = null)
    data class Settings(val vnext: List<Server?>? = null)
    data class Server(val address: String? = null)

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                "stop_vpn" -> {
                    stopVPN()
                }
                "ping"     -> {
                    if (running) {
                        sendBroadcast(Intent("pong"))
                    }
                }
            }
        }
    }

    private fun stopVPN() {
        Tun2socks.stopV2Ray()
        pfd?.close()
        pfd = null
        inputStream = null
        outputStream = null
        running = false
        sendBroadcast(Intent("vpn_stopped"))
        stopSelf()
    }

    class Flow(stream: FileOutputStream?) : PacketFlow {
        private val flowOutputStream = stream
        override fun writePacket(pkt: ByteArray?) {
            flowOutputStream?.write(pkt)
        }
    }

    class Service(service: VpnService) : Tun2socksVpnService {
        private val vpnService = service
        override fun protect(fd: Long): Boolean {
            return vpnService.protect(fd.toInt())
        }
    }

    private fun handlePackets() {
        while (running) {
            try {
                val n = inputStream?.read(buffer.array())
                n?.let { it } ?: return
                if (n > 0) {
                    buffer.limit(n)
                    Tun2socks.inputPacket(buffer.array())
                    buffer.clear()
                }
            } catch (e: Exception) {
                println("failed to read bytes from TUN fd")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(broadcastReceiver, IntentFilter("stop_vpn"))
        registerReceiver(broadcastReceiver, IntentFilter("ping"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val context = applicationContext
        var user: String = ""
        var pass: String = ""
        if (context is IChatInfo) {
            val chatInfo: IChatInfo = context
            user = chatInfo.userId
            pass = chatInfo.accessToken
        }

        val defNode = getDefNode(WalletUtil.MCC_COIN)
        val host = urlToHost(defNode)
        configString = Constants.getConfig(host, user, pass)

        bgThread = thread(start = true) {
            val config = try {
                Klaxon().parse<Config>(configString)
            } catch (e: Exception) {
                sendBroadcast(android.content.Intent("vpn_start_err_config"))
                stopVPN()
                return@thread
            }
            if (config != null) {
                if (config.dns == null || config.dns.servers == null || config.dns.servers.size == 0) {
                    println("must configure dns servers since v2ray will use localhost if there isn't any dns servers")
                    sendBroadcast(Intent("vpn_start_err_dns"))
                    stopVPN()
                    return@thread
                }

                config.dns.servers.forEach {
                    val dnsServer = it as? String
                    if (dnsServer != null && dnsServer == "localhost") {
                        println("using local dns resolver is not allowed since it will cause infinite loop")
                        sendBroadcast(Intent("vpn_start_err_dns"))
                        stopVPN()
                        return@thread
                    }
                }
            } else {
                println("parsing v2ray config failed")
                sendBroadcast(Intent("vpn_start_err"))
                stopVPN()
                return@thread
            }

            val localDns = "223.5.5.5"

            val builder = Builder()
                    .setSession("Kitsunebi")
                    .setMtu(1500)
                    .addAddress("10.233.233.233", 30)
                    .addDnsServer(localDns)
                    .addRoute("0.0.0.0", 0)
                    .addAllowedApplication(this.packageName)
            pfd = builder.establish()

            
            
            
            
            if ((pfd == null) || !Tun2socks.setNonblock(pfd!!.fd.toLong(), false)) {
                println("failed to put tunFd in blocking mode")
                sendBroadcast(Intent("vpn_start_err"))
                stopVPN()
                return@thread
            }

            if (Build.VERSION.SDK_INT >= 28) {
                cm.requestNetwork(defaultNetworkRequest, defaultNetworkCallback)
            }

            inputStream = FileInputStream(pfd!!.fileDescriptor)
            outputStream = FileOutputStream(pfd!!.fileDescriptor)

            val flow = Flow(outputStream)
            val service = Service(this)

            
            val geoipBytes = resources.openRawResource(R.raw.geoip).readBytes()
            val fos = openFileOutput("geoip.dat", Context.MODE_PRIVATE)
            fos.write(geoipBytes)
            fos.close()

            val geositeBytes = resources.openRawResource(R.raw.geosite).readBytes()
            val fos2 = openFileOutput("geosite.dat", Context.MODE_PRIVATE)
            fos2.write(geositeBytes)
            fos2.close()



            var sniffing = "http,tls"
            
            val sniffingList = sniffing.split(",")
            var sniffings = ArrayList<String>()
            for (s in sniffingList) {
                sniffings.add(s.trim())
            }
            sniffing = sniffings.joinToString(",")

            val inboundTag = "tun2socks"

            Tun2socks.setLocalDNS("$localDns:53")
            val ret = Tun2socks.startV2Ray(flow, service, null, configString.toByteArray(), inboundTag, sniffing, filesDir.absolutePath)
            if (ret.toInt() != 0) {
                sendBroadcast(Intent("vpn_start_err_config"))
                stopVPN()
                return@thread
            }

            sendBroadcast(Intent("vpn_started"))

            running = true
            handlePackets()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onRevoke() {
        super.onRevoke()
        stopVPN()
    }
}
