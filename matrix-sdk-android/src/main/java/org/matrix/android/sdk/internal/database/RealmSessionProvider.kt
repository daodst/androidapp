

package org.matrix.android.sdk.internal.database

import android.os.Looper
import androidx.annotation.MainThread
import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.SessionLifecycleObserver
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.SessionScope
import javax.inject.Inject
import kotlin.concurrent.getOrSet


@SessionScope
internal class RealmSessionProvider @Inject constructor(@SessionDatabase private val monarchy: Monarchy) :
        SessionLifecycleObserver {

    private val realmThreadLocal = ThreadLocal<Realm>()

    
    fun <R> withRealm(block: (Realm) -> R): R {
        return getRealmWrapper().withRealm(block)
    }

    @MainThread
    override fun onSessionStarted(session: Session) {
        realmThreadLocal.getOrSet {
            Realm.getInstance(monarchy.realmConfiguration)
        }
    }

    @MainThread
    override fun onSessionStopped(session: Session) {
        realmThreadLocal.get()?.close()
        realmThreadLocal.remove()
    }

    private fun getRealmWrapper(): RealmInstanceWrapper {
        val isOnMainThread = isOnMainThread()
        val realm = if (isOnMainThread) {
            realmThreadLocal.getOrSet {
                Realm.getInstance(monarchy.realmConfiguration)
            }
        } else {
            Realm.getInstance(monarchy.realmConfiguration)
        }
        return RealmInstanceWrapper(realm, closeRealmOnClose = !isOnMainThread)
    }

    private fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()
}
