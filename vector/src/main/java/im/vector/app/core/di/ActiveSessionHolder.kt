

package im.vector.app.core.di

import arrow.core.Option
import im.vector.app.ActiveSessionDataSource
import im.vector.app.core.services.GuardServiceStarter
import im.vector.app.features.call.webrtc.WebRtcCallManager
import im.vector.app.features.crypto.keysrequest.KeyRequestHandler
import im.vector.app.features.crypto.verification.IncomingVerificationRequestHandler
import im.vector.app.features.notifications.PushRuleTriggerListener
import im.vector.app.features.session.SessionListener
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActiveSessionHolder @Inject constructor(private val activeSessionDataSource: ActiveSessionDataSource,
                                              private val keyRequestHandler: KeyRequestHandler,
                                              private val incomingVerificationRequestHandler: IncomingVerificationRequestHandler,
                                              private val callManager: WebRtcCallManager,
                                              private val pushRuleTriggerListener: PushRuleTriggerListener,
                                              private val sessionListener: SessionListener,
                                              private val imageManager: ImageManager,
                                              private val guardServiceStarter: GuardServiceStarter
) {

    private var activeSession: AtomicReference<Session?> = AtomicReference()

    fun setActiveSession(session: Session) {
        Timber.w("setActiveSession of ${session.myUserId}")
        activeSession.set(session)
        activeSessionDataSource.post(Option.just(session))

        keyRequestHandler.start(session)
        incomingVerificationRequestHandler.start(session)
        session.addListener(sessionListener)
        pushRuleTriggerListener.startWithSession(session)
        session.callSignalingService().addCallListener(callManager)
        imageManager.onSessionStarted(session)
        guardServiceStarter.start()
    }

    fun clearActiveSession() {
        
        getSafeActiveSession()?.let {
            Timber.w("clearActiveSession of ${it.myUserId}")
            it.callSignalingService().removeCallListener(callManager)
            it.removeListener(sessionListener)
        }

        activeSession.set(null)
        activeSessionDataSource.post(Option.empty())

        keyRequestHandler.stop()
        incomingVerificationRequestHandler.stop()
        pushRuleTriggerListener.stop()
        guardServiceStarter.stop()
    }

    fun hasActiveSession(): Boolean {
        return activeSession.get() != null
    }

    fun getSafeActiveSession(): Session? {
        return activeSession.get()
    }

    fun getActiveSession(): Session {
        return activeSession.get()
                ?: throw IllegalStateException("You should authenticate before using this")
    }

    
}
