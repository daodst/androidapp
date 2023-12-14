

package org.matrix.android.sdk.internal.session

import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber
import javax.inject.Inject

@SessionScope
internal class SessionListeners @Inject constructor() {

    private val listeners = mutableSetOf<Session.Listener>()

    fun addListener(listener: Session.Listener) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: Session.Listener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

    fun dispatch(session: Session, block: (Session, Session.Listener) -> Unit) {
        synchronized(listeners) {
            listeners.forEach {
                tryOrNull { block(session, it) }
            }
        }
    }
}

internal fun Session?.dispatchTo(sessionListeners: SessionListeners, block: (Session, Session.Listener) -> Unit) {
    if (this == null) {
        Timber.w("You don't have any attached session")
        return
    }
    try {
        sessionListeners.dispatch(this, block)
    } catch (throow : Throwable){
        throow.printStackTrace()
    }

}
