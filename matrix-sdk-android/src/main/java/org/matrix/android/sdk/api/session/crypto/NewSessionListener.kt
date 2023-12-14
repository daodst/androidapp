

package org.matrix.android.sdk.api.session.crypto


interface NewSessionListener {

    
    fun onNewSession(roomId: String?, senderKey: String, sessionId: String)
}
