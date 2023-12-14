

package org.matrix.android.sdk.internal.session

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.SessionLifecycleObserver
import javax.inject.Inject

@SessionScope
internal class SessionCoroutineScopeHolder @Inject constructor() : SessionLifecycleObserver {

    val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    override fun onSessionStopped(session: Session) {
        scope.cancelChildren()
    }

    override fun onClearCache(session: Session) {
        scope.cancelChildren()
    }

    private fun CoroutineScope.cancelChildren() {
        coroutineContext.cancelChildren(CancellationException("Closing session"))
    }
}
