

@file:Suppress("DEPRECATION")

package org.matrix.android.sdk.internal.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.getSystemService
import javax.inject.Inject

internal class NetworkInfoReceiver @Inject constructor() : BroadcastReceiver() {

    var isConnectedCallback: ((Boolean) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        val conn = context.getSystemService<ConnectivityManager>()!!
        val networkInfo: NetworkInfo? = conn.activeNetworkInfo
        isConnectedCallback?.invoke(networkInfo?.isConnected ?: false)
    }
}
