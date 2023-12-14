

package im.vector.app.features.session

import org.matrix.android.sdk.api.session.Session
import kotlin.reflect.KProperty


class SessionScopedProperty<T : Any>(val initializer: (Session) -> T) {

    private val propertyBySessionId = HashMap<String, T>()

    private val sessionListener = object : Session.Listener {

        override fun onSessionStopped(session: Session) {
            synchronized(propertyBySessionId) {
                session.removeListener(this)
                propertyBySessionId.remove(session.sessionId)
            }
        }
    }

    operator fun getValue(thisRef: Session, property: KProperty<*>): T = synchronized(propertyBySessionId) {
        propertyBySessionId.getOrPut(thisRef.sessionId) {
            thisRef.addListener(sessionListener)
            initializer(thisRef)
        }
    }
}
