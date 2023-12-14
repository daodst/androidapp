

package org.matrix.android.sdk.internal.network.ssl

import okhttp3.TlsVersion
import timber.log.Timber
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager



internal class TLSSocketFactory


@Throws(KeyManagementException::class, NoSuchAlgorithmException::class)
constructor(trustPinned: Array<TrustManager>, acceptedTlsVersions: List<TlsVersion>) : SSLSocketFactory() {

    private val internalSSLSocketFactory: SSLSocketFactory
    private val enabledProtocols: Array<String>

    init {
        val context = SSLContext.getInstance("TLS")
        context.init(null, trustPinned, SecureRandom())
        internalSSLSocketFactory = context.socketFactory
        enabledProtocols = Array(acceptedTlsVersions.size) {
            acceptedTlsVersions[it].javaName
        }
    }

    override fun getDefaultCipherSuites(): Array<String> {
        return internalSSLSocketFactory.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return internalSSLSocketFactory.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket? {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket())
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket? {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int): Socket? {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port))
    }

    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket? {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket? {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket? {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(address, port, localAddress, localPort))
    }

    private fun enableTLSOnSocket(socket: Socket?): Socket? {
        if (socket is SSLSocket) {
            val supportedProtocols = socket.supportedProtocols.toSet()
            val filteredEnabledProtocols = enabledProtocols.filter { it in supportedProtocols }

            if (filteredEnabledProtocols.isNotEmpty()) {
                try {
                    socket.enabledProtocols = filteredEnabledProtocols.toTypedArray()
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
        return socket
    }
}
