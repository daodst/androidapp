

package im.vector.app.features.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.features.analytics.AnalyticsTracker
import im.vector.app.features.analytics.extensions.toAnalyticsJoinedRoom
import im.vector.app.features.session.coroutineScope
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.read.ReadService
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class NotificationBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var notificationDrawerManager: NotificationDrawerManager
    @Inject lateinit var activeSessionHolder: ActiveSessionHolder
    @Inject lateinit var analyticsTracker: AnalyticsTracker

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return
        Timber.v("NotificationBroadcastReceiver received : $intent")
        when (intent.action) {
            NotificationUtils.SMART_REPLY_ACTION        ->
                handleSmartReply(intent, context)
            NotificationUtils.DISMISS_ROOM_NOTIF_ACTION ->
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents { it.clearMessagesForRoom(roomId) }
                }
            NotificationUtils.DISMISS_SUMMARY_ACTION    ->
                notificationDrawerManager.clearAllEvents()
            NotificationUtils.MARK_ROOM_READ_ACTION     ->
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents { it.clearMessagesForRoom(roomId) }
                    handleMarkAsRead(roomId)
                }
            NotificationUtils.JOIN_ACTION               -> {
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents { it.clearMemberShipNotificationForRoom(roomId) }
                    handleJoinRoom(roomId)
                }
            }
            NotificationUtils.REJECT_ACTION             -> {
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents { it.clearMemberShipNotificationForRoom(roomId) }
                    handleRejectRoom(roomId)
                }
            }
        }
    }

    private fun handleJoinRoom(roomId: String) {
        activeSessionHolder.getSafeActiveSession()?.let { session ->
            val room = session.getRoom(roomId)
            if (room != null) {
                session.coroutineScope.launch {
                    tryOrNull {
                        session.joinRoom(room.roomId)
                        analyticsTracker.capture(room.roomSummary().toAnalyticsJoinedRoom())
                    }
                }
            }
        }
    }

    private fun handleRejectRoom(roomId: String) {
        activeSessionHolder.getSafeActiveSession()?.let { session ->
            session.coroutineScope.launch {
                tryOrNull { session.leaveRoom(roomId) }
            }
        }
    }

    private fun handleMarkAsRead(roomId: String) {
        activeSessionHolder.getActiveSession().let { session ->
            val room = session.getRoom(roomId)
            if (room != null) {
                session.coroutineScope.launch {
                    tryOrNull { room.markAsRead(ReadService.MarkAsReadParams.READ_RECEIPT) }
                }
            }
        }
    }

    private fun handleSmartReply(intent: Intent, context: Context) {
        val message = getReplyMessage(intent)
        val roomId = intent.getStringExtra(KEY_ROOM_ID)

        if (message.isNullOrBlank() || roomId.isNullOrBlank()) {
            
            
            return
        }
        activeSessionHolder.getActiveSession().let { session ->
            session.getRoom(roomId)?.let { room ->
                sendMatrixEvent(message, session, room, context)
            }
        }
    }

    private fun sendMatrixEvent(message: String, session: Session, room: Room, context: Context?) {
        room.sendTextMessage(message)

        

        val notifiableMessageEvent = NotifiableMessageEvent(
                
                eventId = UUID.randomUUID().toString(),
                editedEventId = null,
                noisy = false,
                timestamp = System.currentTimeMillis(),
                senderName = session.getRoomMember(session.myUserId, room.roomId)?.displayName
                        ?: context?.getString(R.string.notification_sender_me),
                senderId = session.myUserId,
                body = message,
                imageUri = null,
                roomId = room.roomId,
                roomName = room.roomSummary()?.displayName ?: room.roomId,
                roomIsDirect = room.roomSummary()?.isDirect == true,
                outGoingMessage = true,
                canBeReplaced = false
        )

        notificationDrawerManager.updateEvents { it.onNotifiableEventReceived(notifiableMessageEvent) }

        
    }

    private fun getReplyMessage(intent: Intent?): String? {
        if (intent != null) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            if (remoteInput != null) {
                return remoteInput.getCharSequence(KEY_TEXT_REPLY)?.toString()
            }
        }
        return null
    }

    companion object {
        const val KEY_ROOM_ID = "roomID"
        const val KEY_TEXT_REPLY = "key_text_reply"
    }
}
