

package org.matrix.android.sdk.internal.network

import android.annotation.TargetApi
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.core.content.getSystemService
import timber.log.Timber
import javax.inject.Inject

internal interface NetworkCallbackStrategy {
    fun register(hasChanged: () -> Unit)
    fun unregister()
}

internal class FallbackNetworkCallbackStrategy @Inject constructor(private val context: Context,
                                                                   private val networkInfoReceiver: NetworkInfoReceiver) : NetworkCallbackStrategy {

    @Suppress("DEPRECATION")
    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

    override fun register(hasChanged: () -> Unit) {
        networkInfoReceiver.isConnectedCallback = {
            hasChanged()
        }
        context.registerReceiver(networkInfoReceiver, filter)
    }

    override fun unregister() {
        networkInfoReceiver.isConnectedCallback = null
        context.unregisterReceiver(networkInfoReceiver)
    }
}

@TargetApi(Build.VERSION_CODES.N)
internal class PreferredNetworkCallbackStrategy @Inject constructor(context: Context) : NetworkCallbackStrategy {

    private var hasChangedCallback: (() -> Unit)? = null
    private val conn = context.getSystemService<ConnectivityManager>()!!
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onLost(network: Network) {
            hasChangedCallback?.invoke()
        }

        override fun onAvailable(network: Network) {
            hasChangedCallback?.invoke()
        }
    }

    override fun register(hasChanged: () -> Unit) {
        hasChangedCallback = hasChanged
        conn.registerDefaultNetworkCallback(networkCallback)
    }

    override fun unregister() {
        
        val doUnregister = hasChangedCallback != null
        hasChangedCallback = null
        if (doUnregister) {
            
            try {
                conn.unregisterNetworkCallback(networkCallback)
            } catch (t: Throwable) {
                Timber.e(t, "Unable to unregister network callback")
            }
        }
    }
}
