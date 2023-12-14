

package im.vector.app.features.notifications

import android.app.Notification
import androidx.core.app.NotificationCompat
import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import javax.inject.Inject


class SummaryGroupMessageCreator @Inject constructor(
        private val stringProvider: StringProvider,
        private val notificationUtils: NotificationUtils
) {

    fun createSummaryNotification(roomNotifications: List<RoomNotification.Message.Meta>,
                                  invitationNotifications: List<OneShotNotification.Append.Meta>,
                                  simpleNotifications: List<OneShotNotification.Append.Meta>,
                                  useCompleteNotificationFormat: Boolean): Notification {
        val summaryInboxStyle = NotificationCompat.InboxStyle().also { style ->
            roomNotifications.forEach { style.addLine(it.summaryLine) }
            simpleNotifications.forEach { style.addLine(it.summaryLine) }
        }

        val summaryIsNoisy = roomNotifications.any { it.shouldBing } ||
                simpleNotifications.any { it.isNoisy }

        val messageCount = roomNotifications.fold(initial = 0) { acc, current -> acc + current.messageCount }

        val lastMessageTimestamp = roomNotifications.lastOrNull()?.latestTimestamp
                ?: simpleNotifications.last().timestamp

        
        val nbEvents = roomNotifications.size + simpleNotifications.size
        val sumTitle = stringProvider.getQuantityString(R.plurals.notification_compat_summary_title, nbEvents, nbEvents)
        summaryInboxStyle.setBigContentTitle(sumTitle)
                
                .setSummaryText(stringProvider.getQuantityString(R.plurals.notification_unread_notified_messages, nbEvents, nbEvents))
        return if (useCompleteNotificationFormat) {
            notificationUtils.buildSummaryListNotification(
                    summaryInboxStyle,
                    sumTitle,
                    noisy = summaryIsNoisy,
                    lastMessageTimestamp = lastMessageTimestamp
            )
        } else {
            processSimpleGroupSummary(
                    summaryIsNoisy,
                    messageCount,
                    simpleNotifications.size,
                    0,
                    roomNotifications.size,
                    lastMessageTimestamp
            )
        }
    }

    private fun processSimpleGroupSummary(summaryIsNoisy: Boolean,
                                          messageEventsCount: Int,
                                          simpleEventsCount: Int,
                                          invitationEventsCount: Int,
                                          roomCount: Int,
                                          lastMessageTimestamp: Long): Notification {
        
        val messageNotificationCount = messageEventsCount + simpleEventsCount

        val privacyTitle = if (invitationEventsCount > 0) {
            val invitationsStr = stringProvider.getQuantityString(R.plurals.notification_invitations, invitationEventsCount, invitationEventsCount)
            if (messageNotificationCount > 0) {
                
                val messageStr = stringProvider.getQuantityString(
                        R.plurals.room_new_messages_notification,
                        messageNotificationCount, messageNotificationCount
                )
                if (roomCount > 1) {
                    
                    val roomStr = stringProvider.getQuantityString(
                            R.plurals.notification_unread_notified_messages_in_room_rooms,
                            roomCount, roomCount
                    )
                    stringProvider.getString(
                            R.string.notification_unread_notified_messages_in_room_and_invitation,
                            messageStr,
                            roomStr,
                            invitationsStr
                    )
                } else {
                    
                    stringProvider.getString(
                            R.string.notification_unread_notified_messages_and_invitation,
                            messageStr,
                            invitationsStr
                    )
                }
            } else {
                
                invitationsStr
            }
        } else {
            
            val messageStr = stringProvider.getQuantityString(
                    R.plurals.room_new_messages_notification,
                    messageNotificationCount, messageNotificationCount
            )
            if (roomCount > 1) {
                
                val roomStr = stringProvider.getQuantityString(R.plurals.notification_unread_notified_messages_in_room_rooms, roomCount, roomCount)
                stringProvider.getString(R.string.notification_unread_notified_messages_in_room, messageStr, roomStr)
            } else {
                
                messageStr
            }
        }
        return notificationUtils.buildSummaryListNotification(
                style = null,
                compatSummary = privacyTitle,
                noisy = summaryIsNoisy,
                lastMessageTimestamp = lastMessageTimestamp
        )
    }
}
