
package im.vector.app.features.notifications

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.WorkerThread
import im.vector.app.ActiveSessionDataSource
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.utils.FirstThrottler
import im.vector.app.features.displayname.getBestName
import im.vector.app.features.settings.VectorPreferences
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.util.toMatrixItem
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NotificationDrawerManager @Inject constructor(
        private val context: Context,
        private val notificationDisplayer: NotificationDisplayer,
        private val vectorPreferences: VectorPreferences,
        private val activeSessionDataSource: ActiveSessionDataSource,
        private val notifiableEventProcessor: NotifiableEventProcessor,
        private val notificationRenderer: NotificationRenderer,
        private val notificationEventPersistence: NotificationEventPersistence
) {

    private val handlerThread: HandlerThread = HandlerThread("NotificationDrawerManager", Thread.MIN_PRIORITY)
    private var backgroundHandler: Handler

    
    private val currentSession: Session?
        get() = activeSessionDataSource.currentValue?.orNull()

    
    private val notificationState by lazy { createInitialNotificationState() }
    private val avatarSize = context.resources.getDimensionPixelSize(R.dimen.profile_avatar_size)
    private var currentRoomId: String? = null
    private val firstThrottler = FirstThrottler(200)

    private var useCompleteNotificationFormat = vectorPreferences.useCompleteNotificationFormat()

    init {
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)
    }

    private fun createInitialNotificationState(): NotificationState {
        val queuedEvents = notificationEventPersistence.loadEvents(currentSession, factory = { rawEvents ->
            NotificationEventQueue(rawEvents.toMutableList(), seenEventIds = CircularCache.create(cacheSize = 25))
        })
        val renderedEvents = queuedEvents.rawEvents().map { ProcessedEvent(ProcessedEvent.Type.KEEP, it) }.toMutableList()
        return NotificationState(queuedEvents, renderedEvents)
    }

    
    fun NotificationEventQueue.onNotifiableEventReceived(notifiableEvent: NotifiableEvent) {
        if (!vectorPreferences.areNotificationEnabledForDevice()) {
            Timber.i("Notification are disabled for this device")
            return
        }
        
        
        if (BuildConfig.LOW_PRIVACY_LOG_ENABLE) {
            Timber.d("onNotifiableEventReceived(): $notifiableEvent")
        } else {
            Timber.d("onNotifiableEventReceived(): is push: ${notifiableEvent.canBeReplaced}")
        }

        add(notifiableEvent)
    }

    
    fun clearAllEvents() {
        updateEvents { it.clear() }
    }

    
    fun setCurrentRoom(roomId: String?) {
        updateEvents {
            val hasChanged = roomId != currentRoomId
            currentRoomId = roomId
            if (hasChanged && roomId != null) {
                it.clearMessagesForRoom(roomId)
            }
        }
    }

    fun notificationStyleChanged() {
        updateEvents {
            val newSettings = vectorPreferences.useCompleteNotificationFormat()
            if (newSettings != useCompleteNotificationFormat) {
                
                notificationDisplayer.cancelAllNotifications()
                useCompleteNotificationFormat = newSettings
            }
        }
    }

    fun updateEvents(action: NotificationDrawerManager.(NotificationEventQueue) -> Unit) {
        notificationState.updateQueuedEvents(this) { queuedEvents, _ ->
            action(queuedEvents)
        }
        refreshNotificationDrawer()
    }

    private fun refreshNotificationDrawer() {
        
        val canHandle = firstThrottler.canHandle()
        Timber.v("refreshNotificationDrawer(), delay: ${canHandle.waitMillis()} ms")
        backgroundHandler.removeCallbacksAndMessages(null)

        backgroundHandler.postDelayed(
                {
                    try {
                        refreshNotificationDrawerBg()
                    } catch (throwable: Throwable) {
                        
                        Timber.w(throwable, "refreshNotificationDrawerBg failure")
                    }
                },
                canHandle.waitMillis())
    }

    @WorkerThread
    private fun refreshNotificationDrawerBg() {
        Timber.v("refreshNotificationDrawerBg()")
        val eventsToRender = notificationState.updateQueuedEvents(this) { queuedEvents, renderedEvents ->
            notifiableEventProcessor.process(queuedEvents.rawEvents(), currentRoomId, renderedEvents).also {
                queuedEvents.clearAndAdd(it.onlyKeptEvents())
            }
        }

        if (notificationState.hasAlreadyRendered(eventsToRender)) {
            Timber.d("Skipping notification update due to event list not changing")
        } else {
            notificationState.clearAndAddRenderedEvents(eventsToRender)
            val session = currentSession ?: return
            renderEvents(session, eventsToRender)
            persistEvents(session)
        }
    }

    private fun persistEvents(session: Session) {
        notificationState.queuedEvents { queuedEvents ->
            notificationEventPersistence.persistEvents(queuedEvents, session)
        }
    }

    private fun renderEvents(session: Session, eventsToRender: List<ProcessedEvent<NotifiableEvent>>) {
        val user = session.getUser(session.myUserId)
        
        val myUserDisplayName = user?.toMatrixItem()?.getBestName() ?: session.myUserId
        val myUserAvatarUrl = session.contentUrlResolver().resolveThumbnail(
                contentUrl = user?.avatarUrl,
                width = avatarSize,
                height = avatarSize,
                method = ContentUrlResolver.ThumbnailMethod.SCALE
        )
        notificationRenderer.render(session.myUserId, myUserDisplayName, myUserAvatarUrl, useCompleteNotificationFormat, eventsToRender)
    }

    fun shouldIgnoreMessageEventInRoom(roomId: String?): Boolean {
        return currentRoomId != null && roomId == currentRoomId
    }

    companion object {
        const val SUMMARY_NOTIFICATION_ID = 0
        const val ROOM_MESSAGES_NOTIFICATION_ID = 1
        const val ROOM_EVENT_NOTIFICATION_ID = 2
        const val ROOM_INVITATION_NOTIFICATION_ID = 3
    }
}
