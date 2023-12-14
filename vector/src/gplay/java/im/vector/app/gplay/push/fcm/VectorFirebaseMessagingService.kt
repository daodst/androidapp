

package im.vector.app.gplay.push.fcm

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.BuildConfig
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.network.WifiDetector
import im.vector.app.core.pushers.PushersManager
import im.vector.app.features.badge.BadgeProxy
import im.vector.app.features.notifications.NotifiableEventResolver
import im.vector.app.features.notifications.NotificationDrawerManager
import im.vector.app.features.notifications.NotificationUtils
import im.vector.app.features.settings.VectorDataStore
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.push.fcm.FcmHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.logger.LoggerTag
import org.matrix.android.sdk.api.session.Session
import timber.log.Timber
import javax.inject.Inject

private val loggerTag = LoggerTag("Push", LoggerTag.SYNC)


@AndroidEntryPoint
class VectorFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationDrawerManager: NotificationDrawerManager
    @Inject lateinit var notifiableEventResolver: NotifiableEventResolver
    @Inject lateinit var pusherManager: PushersManager
    @Inject lateinit var activeSessionHolder: ActiveSessionHolder
    @Inject lateinit var vectorPreferences: VectorPreferences
    @Inject lateinit var vectorDataStore: VectorDataStore
    @Inject lateinit var wifiDetector: WifiDetector

    private val coroutineScope = CoroutineScope(SupervisorJob())

    
    private val mUIHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    
    override fun onMessageReceived(message: RemoteMessage) {
        if (BuildConfig.LOW_PRIVACY_LOG_ENABLE) {
            Timber.tag(loggerTag.value).d("## onMessageReceived() %s", message.data.toString())
        }
        Timber.tag(loggerTag.value).d("## onMessageReceived() from FCM with priority %s", message.priority)

        runBlocking {
            vectorDataStore.incrementPushCounter()
        }

        
        if (message.data["event_id"] == PushersManager.TEST_EVENT_ID) {
            val intent = Intent(NotificationUtils.PUSH_ACTION)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            return
        }

        if (!vectorPreferences.areNotificationEnabledForDevice()) {
            Timber.tag(loggerTag.value).i("Notification are disabled for this device")
            return
        }

        mUIHandler.post {
            if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                
                Timber.tag(loggerTag.value).d("PUSH received in a foreground state, ignore")
            } else {
                onMessageReceivedInternal(message.data)
            }
        }
    }

    
    override fun onNewToken(refreshedToken: String) {
        Timber.tag(loggerTag.value).i("onNewToken: FCM Token has been updated")
        FcmHelper.storeFcmToken(this, refreshedToken)
        if (vectorPreferences.areNotificationEnabledForDevice() && activeSessionHolder.hasActiveSession()) {
            pusherManager.enqueueRegisterPusherWithFcmKey(refreshedToken)
        }
    }

    
    override fun onDeletedMessages() {
        Timber.tag(loggerTag.value).v("## onDeletedMessages()")
    }

    
    private fun onMessageReceivedInternal(data: Map<String, String>) {
        try {
            if (BuildConfig.LOW_PRIVACY_LOG_ENABLE) {
                Timber.tag(loggerTag.value).d("## onMessageReceivedInternal() : $data")
            } else {
                Timber.tag(loggerTag.value).d("## onMessageReceivedInternal()")
            }

            
            val unreadCount = data["unread"]?.let { Integer.parseInt(it) } ?: 0
            BadgeProxy.updateBadgeCount(applicationContext, unreadCount)

            val session = activeSessionHolder.getSafeActiveSession()

            if (session == null) {
                Timber.tag(loggerTag.value).w("## Can't sync from push, no current session")
            } else {
                val eventId = data["event_id"]
                val roomId = data["room_id"]

                if (isEventAlreadyKnown(eventId, roomId)) {
                    Timber.tag(loggerTag.value).d("Ignoring push, event already known")
                } else {
                    
                    Timber.tag(loggerTag.value).d("Requesting event in fast lane")
                    getEventFastLane(session, roomId, eventId)

                    Timber.tag(loggerTag.value).d("Requesting background sync")
                    session.requireBackgroundSync()
                }
            }
        } catch (e: Exception) {
            Timber.tag(loggerTag.value).e(e, "## onMessageReceivedInternal() failed")
        }
    }

    private fun getEventFastLane(session: Session, roomId: String?, eventId: String?) {
        roomId?.takeIf { it.isNotEmpty() } ?: return
        eventId?.takeIf { it.isNotEmpty() } ?: return

        
        if (notificationDrawerManager.shouldIgnoreMessageEventInRoom(roomId)) {
            return
        }

        if (wifiDetector.isConnectedToWifi().not()) {
            Timber.tag(loggerTag.value).d("No WiFi network, do not get Event")
            return
        }

        coroutineScope.launch {
            Timber.tag(loggerTag.value).d("Fast lane: start request")
            val event = tryOrNull { session.getEvent(roomId, eventId) } ?: return@launch

            val resolvedEvent = notifiableEventResolver.resolveInMemoryEvent(session, event, canBeReplaced = true)

            resolvedEvent
                    ?.also { Timber.tag(loggerTag.value).d("Fast lane: notify drawer") }
                    ?.let {
                        notificationDrawerManager.updateEvents { it.onNotifiableEventReceived(resolvedEvent) }
                    }
        }
    }

    
    
    private fun isEventAlreadyKnown(eventId: String?, roomId: String?): Boolean {
        if (null != eventId && null != roomId) {
            try {
                val session = activeSessionHolder.getSafeActiveSession() ?: return false
                val room = session.getRoom(roomId) ?: return false
                return room.getTimelineEvent(eventId) != null
            } catch (e: Exception) {
                Timber.tag(loggerTag.value).e(e, "## isEventAlreadyKnown() : failed to check if the event was already defined")
            }
        }
        return false
    }
}
