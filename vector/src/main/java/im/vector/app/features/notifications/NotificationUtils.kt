

@file:Suppress("UNUSED_PARAMETER")

package im.vector.app.features.notifications

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.Mavericks
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.extensions.createIgnoredUri
import im.vector.app.core.platform.PendingIntentCompat
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.services.CallService
import im.vector.app.core.utils.startNotificationChannelSettingsIntent
import im.vector.app.features.call.VectorCallActivity
import im.vector.app.features.call.service.CallHeadsUpActionReceiver
import im.vector.app.features.call.webrtc.WebRtcCall
import im.vector.app.features.displayname.getBestName
import im.vector.app.features.home.HomeActivityArgs
import im.vector.app.features.home.room.detail.RoomDetailActivity
import im.vector.app.features.home.room.detail.arguments.TimelineArgs
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.settings.troubleshoot.TestNotificationReceiver
import im.vector.app.features.themes.ThemeUtils
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random


@Singleton
class NotificationUtils @Inject constructor(private val context: Context,
                                            private val stringProvider: StringProvider,
                                            private val vectorPreferences: VectorPreferences) {

    companion object {

        const val FROM_NOTIFICATIONUTILS = "from_NotificationUtils"

        
        fun getMainActivityIntent(from: Context): Intent {
            return try {
                val clazz = Class.forName("com.app.base.activity.MainActivity")
                Intent(from, clazz).apply {
                    putExtra(FROM_NOTIFICATIONUTILS, true)
                }
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            }
        }

        fun newIntent(context: Context,
                      clearNotification: Boolean = false,
                      accountCreation: Boolean = false,
                      existingSession: Boolean = false,
                      inviteNotificationRoomId: String? = null
        ): Intent {
            val args = HomeActivityArgs(
                    clearNotification = clearNotification,
                    accountCreation = accountCreation,
                    hasExistingSession = existingSession,
                    inviteNotificationRoomId = inviteNotificationRoomId
            )

            return getMainActivityIntent(context)
                    .apply {
                        putExtra(Mavericks.KEY_ARG, args)
                    }
        }

        

        
        const val NOTIFICATION_ID_FOREGROUND_SERVICE = 61

        

        const val JOIN_ACTION = "${BuildConfig.APPLICATION_ID}.NotificationActions.JOIN_ACTION"
        const val REJECT_ACTION = "${BuildConfig.APPLICATION_ID}.NotificationActions.REJECT_ACTION"
        private const val QUICK_LAUNCH_ACTION = "${BuildConfig.APPLICATION_ID}.NotificationActions.QUICK_LAUNCH_ACTION"
        const val MARK_ROOM_READ_ACTION = "${BuildConfig.APPLICATION_ID}.NotificationActions.MARK_ROOM_READ_ACTION"
        const val SMART_REPLY_ACTION = "${BuildConfig.APPLICATION_ID}.NotificationActions.SMART_REPLY_ACTION"
        const val DISMISS_SUMMARY_ACTION = "${BuildConfig.APPLICATION_ID}.NotificationActions.DISMISS_SUMMARY_ACTION"
        const val DISMISS_ROOM_NOTIF_ACTION = "${BuildConfig.APPLICATION_ID}.NotificationActions.DISMISS_ROOM_NOTIF_ACTION"
        private const val TAP_TO_VIEW_ACTION = "${BuildConfig.APPLICATION_ID}.NotificationActions.TAP_TO_VIEW_ACTION"
        const val DIAGNOSTIC_ACTION = "${BuildConfig.APPLICATION_ID}.NotificationActions.DIAGNOSTIC"
        const val PUSH_ACTION = "${BuildConfig.APPLICATION_ID}.PUSH"

        

        
        private const val LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID = "LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID"

        private const val NOISY_NOTIFICATION_CHANNEL_ID = "DEFAULT_NOISY_NOTIFICATION_CHANNEL_ID"

        private const val SILENT_NOTIFICATION_CHANNEL_ID = "DEFAULT_SILENT_NOTIFICATION_CHANNEL_ID_V2"
        private const val CALL_NOTIFICATION_CHANNEL_ID = "CALL_NOTIFICATION_CHANNEL_ID_V2"

        fun supportNotificationChannels() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

        fun openSystemSettingsForSilentCategory(fragment: Fragment) {
            startNotificationChannelSettingsIntent(fragment, SILENT_NOTIFICATION_CHANNEL_ID)
        }

        fun openSystemSettingsForNoisyCategory(fragment: Fragment) {
            startNotificationChannelSettingsIntent(fragment, NOISY_NOTIFICATION_CHANNEL_ID)
        }

        fun openSystemSettingsForCallCategory(fragment: Fragment) {
            startNotificationChannelSettingsIntent(fragment, CALL_NOTIFICATION_CHANNEL_ID)
        }
    }

    private val notificationManager = NotificationManagerCompat.from(context)

    

    
    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationChannels() {
        if (!supportNotificationChannels()) {
            return
        }

        val accentColor = ContextCompat.getColor(context, R.color.notification_accent_color)

        
        
        
        
        for (channel in notificationManager.notificationChannels) {
            val channelId = channel.id
            val legacyBaseName = "DEFAULT_NOISY_NOTIFICATION_CHANNEL_ID_BASE"
            if (channelId.startsWith(legacyBaseName)) {
                notificationManager.deleteNotificationChannel(channelId)
            }
        }
        
        for (channelId in listOf("DEFAULT_SILENT_NOTIFICATION_CHANNEL_ID", "CALL_NOTIFICATION_CHANNEL_ID")) {
            notificationManager.getNotificationChannel(channelId)?.let {
                notificationManager.deleteNotificationChannel(channelId)
            }
        }

        
        notificationManager.createNotificationChannel(NotificationChannel(
                NOISY_NOTIFICATION_CHANNEL_ID,
                stringProvider.getString(R.string.notification_noisy_notifications).ifEmpty { "Noisy notifications" },
                NotificationManager.IMPORTANCE_DEFAULT
        )
                .apply {
                    description = stringProvider.getString(R.string.notification_noisy_notifications)
                    enableVibration(true)
                    enableLights(true)
                    lightColor = accentColor
                })

        
        notificationManager.createNotificationChannel(NotificationChannel(
                SILENT_NOTIFICATION_CHANNEL_ID,
                stringProvider.getString(R.string.notification_silent_notifications).ifEmpty { "Silent notifications" },
                NotificationManager.IMPORTANCE_LOW
        )
                .apply {
                    description = stringProvider.getString(R.string.notification_silent_notifications)
                    setSound(null, null)
                    enableLights(true)
                    lightColor = accentColor
                })

        notificationManager.createNotificationChannel(NotificationChannel(
                LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID,
                stringProvider.getString(R.string.notification_listening_for_events).ifEmpty { "Listening for events" },
                NotificationManager.IMPORTANCE_MIN
        )
                .apply {
                    description = stringProvider.getString(R.string.notification_listening_for_events)
                    setSound(null, null)
                    setShowBadge(false)
                })

        notificationManager.createNotificationChannel(NotificationChannel(
                CALL_NOTIFICATION_CHANNEL_ID,
                stringProvider.getString(R.string.call).ifEmpty { "Call" },
                NotificationManager.IMPORTANCE_HIGH
        )
                .apply {
                    description = stringProvider.getString(R.string.call)
                    setSound(null, null)
                    enableLights(true)
                    lightColor = accentColor
                })
    }

    fun getChannel(channelId: String): NotificationChannel? {
        return notificationManager.getNotificationChannel(channelId)
    }

    
    @SuppressLint("NewApi")
    fun buildForegroundServiceNotification(@StringRes subTitleResId: Int, withProgress: Boolean = true): Notification {
        
        val i = getMainActivityIntent(context)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pi = PendingIntent.getActivity(context, 0, i, PendingIntentCompat.FLAG_IMMUTABLE)

        val accentColor = ContextCompat.getColor(context, R.color.notification_accent_color)

        val builder = NotificationCompat.Builder(context, LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(stringProvider.getString(subTitleResId))
                .setSmallIcon(R.drawable.sync)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setColor(accentColor)
                .setContentIntent(pi)
                .apply {
                    if (withProgress) {
                        setProgress(0, 0, true)
                    }
                }

        
        builder.priority = NotificationCompat.PRIORITY_LOW

        val notification = builder.build()

        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            
            

            
            
            try {
                val deprecatedMethod = notification.javaClass
                        .getMethod(
                                "setLatestEventInfo",
                                Context::class.java,
                                CharSequence::class.java,
                                CharSequence::class.java,
                                PendingIntent::class.java
                        )
                deprecatedMethod.invoke(notification, context, stringProvider.getString(R.string.app_name), stringProvider.getString(subTitleResId), pi)
            } catch (ex: Exception) {
                Timber.e(ex, "## buildNotification(): Exception - setLatestEventInfo() Msg=")
            }
        }
        return notification
    }

    fun getChannelForIncomingCall(fromBg: Boolean): NotificationChannel? {
        val notificationChannel = if (fromBg) CALL_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID
        return getChannel(notificationChannel)
    }

    
    @SuppressLint("NewApi")
    fun buildIncomingCallNotification(call: WebRtcCall,
                                      title: String,
                                      fromBg: Boolean): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.notification_accent_color)
        val notificationChannel = if (fromBg) CALL_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID
        val builder = NotificationCompat.Builder(context, notificationChannel)
                .setContentTitle(ensureTitleNotEmpty(title))
                .apply {
                    if (call.mxCall.isVideoCall) {
                        setContentText(stringProvider.getString(R.string.incoming_video_call))
                        setSmallIcon(R.drawable.ic_call_answer_video)
                    } else {
                        setContentText(stringProvider.getString(R.string.incoming_voice_call))
                        setSmallIcon(R.drawable.ic_call_answer)
                    }
                }
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setColor(ThemeUtils.getColor(context, android.R.attr.colorPrimary))
                .setLights(accentColor, 500, 500)
                .setOngoing(true)

        val contentIntent = VectorCallActivity.newIntent(
                context = context,
                call = call,
                mode = VectorCallActivity.INCOMING_RINGING
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = createIgnoredUri(call.callId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                contentIntent,
                PendingIntentCompat.FLAG_IMMUTABLE
        )

        val answerCallPendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(getMainActivityIntent(context))
                .addNextIntent(
                        VectorCallActivity.newIntent(
                                context = context,
                                call = call,
                                mode = VectorCallActivity.INCOMING_ACCEPT
                        )
                )
                .getPendingIntent(System.currentTimeMillis().toInt(), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE)

        val rejectCallPendingIntent = buildRejectCallPendingIntent(call.callId)

        builder.addAction(
                NotificationCompat.Action(
                        IconCompat.createWithResource(context, R.drawable.ic_call_hangup)
                                .setTint(ThemeUtils.getColor(context, R.attr.colorError)),
                        getActionText(R.string.call_notification_reject, R.attr.colorError),
                        rejectCallPendingIntent
                )
        )

        builder.addAction(
                NotificationCompat.Action(
                        R.drawable.ic_call_answer,
                        getActionText(R.string.call_notification_answer, R.attr.colorPrimary),
                        answerCallPendingIntent
                )
        )
        if (fromBg) {
            
            builder.priority = NotificationCompat.PRIORITY_HIGH
            builder.setFullScreenIntent(contentPendingIntent, true)
        }
        return builder.build()
    }

    fun buildOutgoingRingingCallNotification(call: WebRtcCall,
                                             title: String): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.notification_accent_color)
        val builder = NotificationCompat.Builder(context, SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(ensureTitleNotEmpty(title))
                .apply {
                    setContentText(stringProvider.getString(R.string.call_ringing))
                    if (call.mxCall.isVideoCall) {
                        setSmallIcon(R.drawable.ic_call_answer_video)
                    } else {
                        setSmallIcon(R.drawable.ic_call_answer)
                    }
                }
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setLights(accentColor, 500, 500)
                .setColor(ThemeUtils.getColor(context, android.R.attr.colorPrimary))
                .setOngoing(true)

        val contentIntent = VectorCallActivity.newIntent(
                context = context,
                call = call,
                mode = null
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data = createIgnoredUri(call.callId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                contentIntent,
                PendingIntentCompat.FLAG_IMMUTABLE
        )

        val rejectCallPendingIntent = buildRejectCallPendingIntent(call.callId)

        builder.addAction(
                NotificationCompat.Action(
                        IconCompat.createWithResource(context, R.drawable.ic_call_hangup)
                                .setTint(ThemeUtils.getColor(context, R.attr.colorError)),
                        getActionText(R.string.call_notification_hangup, R.attr.colorError),
                        rejectCallPendingIntent
                )
        )
        builder.setContentIntent(contentPendingIntent)

        return builder.build()
    }

    
    @SuppressLint("NewApi")
    fun buildPendingCallNotification(call: WebRtcCall,
                                     title: String): Notification {
        val builder = NotificationCompat.Builder(context, SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(ensureTitleNotEmpty(title))
                .apply {
                    if (call.mxCall.isVideoCall) {
                        setContentText(stringProvider.getString(R.string.video_call_in_progress))
                        setSmallIcon(R.drawable.ic_call_answer_video)
                    } else {
                        setContentText(stringProvider.getString(R.string.call_in_progress))
                        setSmallIcon(R.drawable.ic_call_answer)
                    }
                }
                .setColor(ThemeUtils.getColor(context, android.R.attr.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_CALL)

        val rejectCallPendingIntent = buildRejectCallPendingIntent(call.callId)

        builder.addAction(
                NotificationCompat.Action(
                        IconCompat.createWithResource(context, R.drawable.ic_call_hangup)
                                .setTint(ThemeUtils.getColor(context, R.attr.colorError)),
                        getActionText(R.string.call_notification_hangup, R.attr.colorError),
                        rejectCallPendingIntent
                )
        )

        val contentPendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(getMainActivityIntent(context))
                .addNextIntent(VectorCallActivity.newIntent(context, call, null))
                .getPendingIntent(System.currentTimeMillis().toInt(), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE)

        builder.setContentIntent(contentPendingIntent)

        return builder.build()
    }

    private fun buildRejectCallPendingIntent(callId: String): PendingIntent {
        
        val rejectCallActionReceiver = Intent(context, CallHeadsUpActionReceiver::class.java).apply {
            putExtra(CallHeadsUpActionReceiver.EXTRA_CALL_ID, callId)
            putExtra(CallHeadsUpActionReceiver.EXTRA_CALL_ACTION_KEY, CallHeadsUpActionReceiver.CALL_ACTION_REJECT)
        }

        return PendingIntent.getBroadcast(
                context,
                System.currentTimeMillis().toInt(),
                rejectCallActionReceiver,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )
    }

    
    fun buildCallEndedNotification(isVideoCall: Boolean): Notification {
        return NotificationCompat.Builder(context, SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(stringProvider.getString(R.string.call_ended))
                .apply {
                    if (isVideoCall) {
                        setSmallIcon(R.drawable.ic_call_answer_video)
                    } else {
                        setSmallIcon(R.drawable.ic_call_answer)
                    }
                }
                .setTimeoutAfter(1)
                .setColor(ThemeUtils.getColor(context, android.R.attr.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .build()
    }

    
    fun buildCallMissedNotification(callInformation: CallService.CallInformation): Notification {
        val builder = NotificationCompat.Builder(context, SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(callInformation.opponentMatrixItem?.getBestName() ?: callInformation.opponentUserId)
                .apply {
                    if (callInformation.isVideoCall) {
                        setContentText(stringProvider.getQuantityString(R.plurals.missed_video_call, 1, 1))
                        setSmallIcon(R.drawable.ic_missed_video_call)
                    } else {
                        setContentText(stringProvider.getQuantityString(R.plurals.missed_audio_call, 1, 1))
                        setSmallIcon(R.drawable.ic_missed_voice_call)
                    }
                }
                .setShowWhen(true)
                .setColor(ThemeUtils.getColor(context, android.R.attr.colorPrimary))
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_CALL)

        val contentPendingIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(getMainActivityIntent(context))
                .addNextIntent(RoomDetailActivity.newIntent(context, TimelineArgs(callInformation.nativeRoomId)))
                .getPendingIntent(System.currentTimeMillis().toInt(), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE)

        builder.setContentIntent(contentPendingIntent)
        return builder.build()
    }

    
    fun buildLiveLocationSharingNotification(): Notification {
        return NotificationCompat.Builder(context, SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(stringProvider.getString(R.string.live_location_sharing_notification_title))
                .setContentText(stringProvider.getString(R.string.live_location_sharing_notification_description))
                .setSmallIcon(R.drawable.ic_attachment_location_live_white)
                .setColor(ThemeUtils.getColor(context, android.R.attr.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_LOCATION_SHARING)
                .setContentIntent(buildOpenHomePendingIntentForSummary())
                .build()
    }

    
    fun buildScreenSharingNotification(): Notification {
        return NotificationCompat.Builder(context, SILENT_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(stringProvider.getString(R.string.screen_sharing_notification_title))
                .setContentText(stringProvider.getString(R.string.screen_sharing_notification_description))
                .setSmallIcon(R.drawable.ic_share_screen)
                .setColor(ThemeUtils.getColor(context, android.R.attr.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(buildOpenHomePendingIntentForSummary())
                .build()
    }

    fun buildDownloadFileNotification(uri: Uri, fileName: String, mimeType: String): Notification {
        return NotificationCompat.Builder(context, SILENT_NOTIFICATION_CHANNEL_ID)
                .setGroup(stringProvider.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_download)
                .setContentText(stringProvider.getString(R.string.downloaded_file, fileName))
                .setAutoCancel(true)
                .apply {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, mimeType)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    PendingIntent.getActivity(
                            context,
                            System.currentTimeMillis().toInt(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                    ).let {
                        setContentIntent(it)
                    }
                }
                .build()
    }

    
    fun buildMessagesListNotification(messageStyle: NotificationCompat.MessagingStyle,
                                      roomInfo: RoomEventGroupInfo,
                                      largeIcon: Bitmap?,
                                      lastMessageTimestamp: Long,
                                      senderDisplayNameForReplyCompat: String?,
                                      tickerText: String): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.notification_accent_color)
        
        val openRoomIntent = buildOpenRoomIntent(roomInfo.roomId)
        val smallIcon = R.mipmap.ic_launcher

        val channelID = if (roomInfo.shouldBing) NOISY_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID
        return NotificationCompat.Builder(context, channelID)
                .setOnlyAlertOnce(true)
                .setWhen(lastMessageTimestamp)
                
                .setStyle(messageStyle)
                
                
                
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                
                .setShortcutId(roomInfo.roomId)
                
                .setContentTitle(roomInfo.roomDisplayName)
                
                .setContentText(stringProvider.getString(R.string.notification_new_messages))
                
                .setSubText(stringProvider.getQuantityString(R.plurals.room_new_messages_notification, messageStyle.messages.size, messageStyle.messages.size))
                
                
                
                .setGroup(stringProvider.getString(R.string.app_name))
                
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                .setSmallIcon(smallIcon)
                
                .setColor(accentColor)
                
                
                
                .apply {
                    if (roomInfo.shouldBing) {
                        
                        priority = NotificationCompat.PRIORITY_DEFAULT
                        vectorPreferences.getNotificationRingTone()?.let {
                            setSound(it)
                        }
                        setLights(accentColor, 500, 500)
                    } else {
                        priority = NotificationCompat.PRIORITY_LOW
                    }

                    
                    
                    val markRoomReadIntent = Intent(context, NotificationBroadcastReceiver::class.java)
                    markRoomReadIntent.action = MARK_ROOM_READ_ACTION
                    markRoomReadIntent.data = createIgnoredUri(roomInfo.roomId)
                    markRoomReadIntent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomInfo.roomId)
                    val markRoomReadPendingIntent = PendingIntent.getBroadcast(
                            context,
                            System.currentTimeMillis().toInt(),
                            markRoomReadIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                    )

                    NotificationCompat.Action.Builder(
                            R.drawable.ic_material_done_all_white,
                            stringProvider.getString(R.string.action_mark_room_read), markRoomReadPendingIntent
                    )
                            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
                            .setShowsUserInterface(false)
                            .build()
                            .let { addAction(it) }

                    
                    if (!roomInfo.hasSmartReplyError) {
                        buildQuickReplyIntent(roomInfo.roomId, senderDisplayNameForReplyCompat)?.let { replyPendingIntent ->
                            val remoteInput = RemoteInput.Builder(NotificationBroadcastReceiver.KEY_TEXT_REPLY)
                                    .setLabel(stringProvider.getString(R.string.action_quick_reply))
                                    .build()
                            NotificationCompat.Action.Builder(
                                    R.drawable.vector_notification_quick_reply,
                                    stringProvider.getString(R.string.action_quick_reply), replyPendingIntent
                            )
                                    .addRemoteInput(remoteInput)
                                    .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
                                    .setShowsUserInterface(false)
                                    .build()
                                    .let { addAction(it) }
                        }
                    }

                    if (openRoomIntent != null) {
                        setContentIntent(openRoomIntent)
                    }

                    if (largeIcon != null) {
                        setLargeIcon(largeIcon)
                    }

                    val intent = Intent(context, NotificationBroadcastReceiver::class.java)
                    intent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomInfo.roomId)
                    intent.action = DISMISS_ROOM_NOTIF_ACTION
                    val pendingIntent = PendingIntent.getBroadcast(
                            context.applicationContext,
                            System.currentTimeMillis().toInt(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                    )
                    setDeleteIntent(pendingIntent)
                }
                .setTicker(tickerText)
                .build()
    }

    
    fun buildRoomInvitationNotification(inviteNotifiableEvent: InviteNotifiableEvent,
                                        matrixId: String): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.notification_accent_color)
        
        val smallIcon = R.mipmap.ic_launcher

        val channelID = if (inviteNotifiableEvent.noisy) NOISY_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID

        return NotificationCompat.Builder(context, channelID)
                .setOnlyAlertOnce(true)
                .setContentTitle(inviteNotifiableEvent.roomName ?: stringProvider.getString(R.string.app_name))
                .setContentText(inviteNotifiableEvent.description)
                .setGroup(stringProvider.getString(R.string.app_name))
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                .setSmallIcon(smallIcon)
                .setColor(accentColor)
                .apply {
                    val roomId = inviteNotifiableEvent.roomId
                    
                    val rejectIntent = Intent(context, NotificationBroadcastReceiver::class.java)
                    rejectIntent.action = REJECT_ACTION
                    rejectIntent.data = createIgnoredUri("$roomId&$matrixId")
                    rejectIntent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomId)
                    val rejectIntentPendingIntent = PendingIntent.getBroadcast(
                            context,
                            System.currentTimeMillis().toInt(),
                            rejectIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                    )

                    addAction(
                            R.drawable.vector_notification_reject_invitation,
                            stringProvider.getString(R.string.action_reject),
                            rejectIntentPendingIntent
                    )

                    
                    val joinIntent = Intent(context, NotificationBroadcastReceiver::class.java)
                    joinIntent.action = JOIN_ACTION
                    joinIntent.data = createIgnoredUri("$roomId&$matrixId")
                    joinIntent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomId)
                    val joinIntentPendingIntent = PendingIntent.getBroadcast(
                            context,
                            System.currentTimeMillis().toInt(),
                            joinIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                    )
                    addAction(
                            R.drawable.vector_notification_accept_invitation,
                            stringProvider.getString(R.string.action_join),
                            joinIntentPendingIntent
                    )

                    val contentIntent = newIntent(context, inviteNotificationRoomId = inviteNotifiableEvent.roomId)
                    contentIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    
                    contentIntent.data = createIgnoredUri(inviteNotifiableEvent.eventId)
                    setContentIntent(PendingIntent.getActivity(context, 0, contentIntent, PendingIntentCompat.FLAG_IMMUTABLE))

                    if (inviteNotifiableEvent.noisy) {
                        
                        priority = NotificationCompat.PRIORITY_DEFAULT
                        vectorPreferences.getNotificationRingTone()?.let {
                            setSound(it)
                        }
                        setLights(accentColor, 500, 500)
                    } else {
                        priority = NotificationCompat.PRIORITY_LOW
                    }
                    setAutoCancel(true)
                }
                .build()
    }

    fun buildSimpleEventNotification(simpleNotifiableEvent: SimpleNotifiableEvent,
                                     matrixId: String): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.notification_accent_color)
        
        val smallIcon = R.mipmap.ic_launcher

        val channelID = if (simpleNotifiableEvent.noisy) NOISY_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID

        return NotificationCompat.Builder(context, channelID)
                .setOnlyAlertOnce(true)
                .setContentTitle(stringProvider.getString(R.string.app_name))
                .setContentText(simpleNotifiableEvent.description)
                .setGroup(stringProvider.getString(R.string.app_name))
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
                .setSmallIcon(smallIcon)
                .setColor(accentColor)
                .setAutoCancel(true)
                .apply {
                    val contentIntent = getMainActivityIntent(context)
                    contentIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    
                    contentIntent.data = createIgnoredUri(simpleNotifiableEvent.eventId)
                    setContentIntent(PendingIntent.getActivity(context, 0, contentIntent, PendingIntentCompat.FLAG_IMMUTABLE))

                    if (simpleNotifiableEvent.noisy) {
                        
                        priority = NotificationCompat.PRIORITY_DEFAULT
                        vectorPreferences.getNotificationRingTone()?.let {
                            setSound(it)
                        }
                        setLights(accentColor, 500, 500)
                    } else {
                        priority = NotificationCompat.PRIORITY_LOW
                    }
                    setAutoCancel(true)
                }
                .build()
    }

    private fun buildOpenRoomIntent(roomId: String): PendingIntent? {
        val roomIntentTap = RoomDetailActivity.newIntent(context, TimelineArgs(roomId = roomId, switchToParentSpace = true))
        roomIntentTap.action = TAP_TO_VIEW_ACTION
        
        roomIntentTap.data = createIgnoredUri("openRoom?$roomId")

        
        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(getMainActivityIntent(context))
                .addNextIntent(roomIntentTap)
                .getPendingIntent(
                        System.currentTimeMillis().toInt(),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                )
    }

    private fun buildOpenHomePendingIntentForSummary(): PendingIntent {
        val intent = newIntent(context, clearNotification = true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.data = createIgnoredUri("tapSummary")
        return PendingIntent.getActivity(
                context,
                Random.nextInt(1000),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )
    }

    
    private fun buildQuickReplyIntent(roomId: String, senderName: String?): PendingIntent? {
        val intent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = Intent(context, NotificationBroadcastReceiver::class.java)
            intent.action = SMART_REPLY_ACTION
            intent.data = createIgnoredUri(roomId)
            intent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomId)
            return PendingIntent.getBroadcast(
                    context,
                    System.currentTimeMillis().toInt(),
                    intent,
                    
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_MUTABLE
            )
        } else {
            
        }
        return null
    }

    
    
    fun buildSummaryListNotification(style: NotificationCompat.InboxStyle?,
                                     compatSummary: String,
                                     noisy: Boolean,
                                     lastMessageTimestamp: Long): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.notification_accent_color)
        val smallIcon = R.mipmap.ic_launcher

        return NotificationCompat.Builder(context, if (noisy) NOISY_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID)
                .setOnlyAlertOnce(true)
                
                .setWhen(lastMessageTimestamp)
                .setStyle(style)
                .setContentTitle(stringProvider.getString(R.string.app_name))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(smallIcon)
                
                .setContentText(compatSummary)
                .setGroup(stringProvider.getString(R.string.app_name))
                
                .setGroupSummary(true)
                .setColor(accentColor)
                .apply {
                    if (noisy) {
                        
                        priority = NotificationCompat.PRIORITY_DEFAULT
                        vectorPreferences.getNotificationRingTone()?.let {
                            setSound(it)
                        }
                        setLights(accentColor, 500, 500)
                    } else {
                        
                        priority = NotificationCompat.PRIORITY_LOW
                    }
                }
                .setContentIntent(buildOpenHomePendingIntentForSummary())
                .setDeleteIntent(getDismissSummaryPendingIntent())
                .build()
    }

    private fun getDismissSummaryPendingIntent(): PendingIntent {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.action = DISMISS_SUMMARY_ACTION
        intent.data = createIgnoredUri("deleteSummary")
        return PendingIntent.getBroadcast(
                context.applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )
    }

    fun showNotificationMessage(tag: String?, id: Int, notification: Notification) {
        notificationManager.notify(tag, id, notification)
    }

    fun cancelNotificationMessage(tag: String?, id: Int) {
        notificationManager.cancel(tag, id)
    }

    
    fun cancelNotificationForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID_FOREGROUND_SERVICE)
    }

    
    fun cancelAllNotifications() {
        
        try {
            notificationManager.cancelAll()
        } catch (e: Exception) {
            Timber.e(e, "## cancelAllNotifications() failed")
        }
    }

    fun displayDiagnosticNotification() {
        val testActionIntent = Intent(context, TestNotificationReceiver::class.java)
        testActionIntent.action = DIAGNOSTIC_ACTION
        val testPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                testActionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )

        notificationManager.notify(
                "DIAGNOSTIC",
                888,
                NotificationCompat.Builder(context, NOISY_NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(stringProvider.getString(R.string.app_name))
                        .setContentText(stringProvider.getString(R.string.settings_troubleshoot_test_push_notification_content))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(getBitmap(context, R.mipmap.ic_launcher))
                        .setColor(ContextCompat.getColor(context, R.color.notification_accent_color))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setAutoCancel(true)
                        .setContentIntent(testPendingIntent)
                        .build()
        )
    }

    private fun getBitmap(context: Context, @DrawableRes drawableRes: Int): Bitmap? {
        val drawable = ResourcesCompat.getDrawable(context.resources, drawableRes, null) ?: return null
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    
    fun isDoNotDisturbModeOn(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false
        }

        
        val setting = context.getSystemService<NotificationManager>()!!.currentInterruptionFilter

        return setting == NotificationManager.INTERRUPTION_FILTER_NONE ||
                setting == NotificationManager.INTERRUPTION_FILTER_ALARMS
    }

    private fun getActionText(@StringRes stringRes: Int, @AttrRes colorRes: Int): Spannable {
        return SpannableString(context.getText(stringRes)).apply {
            val foregroundColorSpan = ForegroundColorSpan(ThemeUtils.getColor(context, colorRes))
            setSpan(foregroundColorSpan, 0, length, 0)
        }
    }

    private fun ensureTitleNotEmpty(title: String?): CharSequence {
        if (title.isNullOrBlank()) {
            return stringProvider.getString(R.string.app_name)
        }

        return title
    }
}
