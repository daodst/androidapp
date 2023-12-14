

package org.matrix.android.sdk.internal.session.room.send.queue

import org.matrix.android.sdk.api.auth.data.SessionParams
import timber.log.Timber
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

internal class HomeServerAvailabilityChecker(val sessionParams: SessionParams) {

    fun check(): Boolean {
        val host = sessionParams.homeServerConnectionConfig.homeServerUriBase.host ?: return false
        val port = sessionParams.homeServerConnectionConfig.homeServerUriBase.port.takeIf { it != -1 } ?: 80
        val timeout = 30_000
        try {
            Socket().use { socket ->
                val inetAddress: InetAddress = InetAddress.getByName(host)
                val inetSocketAddress = InetSocketAddress(inetAddress, port)
                socket.connect(inetSocketAddress, timeout)
                return true
            }
        } catch (e: IOException) {
            Timber.v("## EventSender isHostAvailable failure ${e.localizedMessage}")
            return false
        }
    }
}
