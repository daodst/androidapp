

package org.matrix.android.sdk.internal.crypto.model

import kotlinx.coroutines.sync.Mutex
import org.matrix.olm.OlmSession


internal data class OlmSessionWrapper(
        
        val olmSession: OlmSession,
        
        var lastReceivedMessageTs: Long = 0,

        val mutex: Mutex = Mutex()
) {

    
    fun onMessageReceived() {
        lastReceivedMessageTs = System.currentTimeMillis()
    }
}
