
package im.vector.app.features.settings

import android.content.Context
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.BoolRes
import androidx.core.content.edit
import com.squareup.seismic.ShakeDetector
import im.vector.app.BuildConfig
import im.vector.app.R
import im.vector.app.core.di.DefaultSharedPreferences
import im.vector.app.features.disclaimer.SHARED_PREF_KEY
import im.vector.app.features.homeserver.ServerUrlsRepository
import im.vector.app.features.themes.ThemeUtils
import org.matrix.android.sdk.api.extensions.tryOrNull
import timber.log.Timber
import javax.inject.Inject

class VectorPreferences @Inject constructor(private val context: Context) {

    companion object {
        const val SETTINGS_HELP_PREFERENCE_KEY = "SETTINGS_HELP_PREFERENCE_KEY"
        const val SETTINGS_CHANGE_PASSWORD_PREFERENCE_KEY = "SETTINGS_CHANGE_PASSWORD_PREFERENCE_KEY"
        const val SETTINGS_VERSION_PREFERENCE_KEY = "SETTINGS_VERSION_PREFERENCE_KEY"
        const val SETTINGS_SDK_VERSION_PREFERENCE_KEY = "SETTINGS_SDK_VERSION_PREFERENCE_KEY"
        const val SETTINGS_OLM_VERSION_PREFERENCE_KEY = "SETTINGS_OLM_VERSION_PREFERENCE_KEY"
        const val SETTINGS_LOGGED_IN_PREFERENCE_KEY = "SETTINGS_LOGGED_IN_PREFERENCE_KEY"
        const val SETTINGS_HOME_SERVER_PREFERENCE_KEY = "SETTINGS_HOME_SERVER_PREFERENCE_KEY"
        const val SETTINGS_IDENTITY_SERVER_PREFERENCE_KEY = "SETTINGS_IDENTITY_SERVER_PREFERENCE_KEY"
        const val SETTINGS_DISCOVERY_PREFERENCE_KEY = "SETTINGS_DISCOVERY_PREFERENCE_KEY"
        const val SETTINGS_EMAILS_AND_PHONE_NUMBERS_PREFERENCE_KEY = "SETTINGS_EMAILS_AND_PHONE_NUMBERS_PREFERENCE_KEY"

        const val SETTINGS_CLEAR_CACHE_PREFERENCE_KEY = "SETTINGS_CLEAR_CACHE_PREFERENCE_KEY"
        const val SETTINGS_CLEAR_MEDIA_CACHE_PREFERENCE_KEY = "SETTINGS_CLEAR_MEDIA_CACHE_PREFERENCE_KEY"
        const val SETTINGS_USER_SETTINGS_PREFERENCE_KEY = "SETTINGS_USER_SETTINGS_PREFERENCE_KEY"
        const val SETTINGS_CONTACT_PREFERENCE_KEYS = "SETTINGS_CONTACT_PREFERENCE_KEYS"
        const val SETTINGS_NOTIFICATIONS_TARGETS_PREFERENCE_KEY = "SETTINGS_NOTIFICATIONS_TARGETS_PREFERENCE_KEY"
        const val SETTINGS_NOTIFICATIONS_TARGET_DIVIDER_PREFERENCE_KEY = "SETTINGS_NOTIFICATIONS_TARGET_DIVIDER_PREFERENCE_KEY"
        const val SETTINGS_FDROID_BACKGROUND_SYNC_MODE = "SETTINGS_FDROID_BACKGROUND_SYNC_MODE"
        const val SETTINGS_BACKGROUND_SYNC_PREFERENCE_KEY = "SETTINGS_BACKGROUND_SYNC_PREFERENCE_KEY"
        const val SETTINGS_BACKGROUND_SYNC_DIVIDER_PREFERENCE_KEY = "SETTINGS_BACKGROUND_SYNC_DIVIDER_PREFERENCE_KEY"
        const val SETTINGS_LABS_PREFERENCE_KEY = "SETTINGS_LABS_PREFERENCE_KEY"
        const val SETTINGS_CRYPTOGRAPHY_PREFERENCE_KEY = "SETTINGS_CRYPTOGRAPHY_PREFERENCE_KEY"
        const val SETTINGS_CRYPTOGRAPHY_DIVIDER_PREFERENCE_KEY = "SETTINGS_CRYPTOGRAPHY_DIVIDER_PREFERENCE_KEY"
        const val SETTINGS_CRYPTOGRAPHY_MANAGE_PREFERENCE_KEY = "SETTINGS_CRYPTOGRAPHY_MANAGE_PREFERENCE_KEY"
        const val SETTINGS_CRYPTOGRAPHY_MANAGE_DIVIDER_PREFERENCE_KEY = "SETTINGS_CRYPTOGRAPHY_MANAGE_DIVIDER_PREFERENCE_KEY"
        const val SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_PREFERENCE_KEY = "SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_PREFERENCE_KEY"
        const val SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_IS_ACTIVE_PREFERENCE_KEY = "SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_IS_ACTIVE_PREFERENCE_KEY"
        const val SETTINGS_ENCRYPTION_CROSS_SIGNING_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_CROSS_SIGNING_PREFERENCE_KEY"
        const val SETTINGS_ENCRYPTION_EXPORT_E2E_ROOM_KEYS_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_EXPORT_E2E_ROOM_KEYS_PREFERENCE_KEY"
        const val SETTINGS_ENCRYPTION_IMPORT_E2E_ROOM_KEYS_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_IMPORT_E2E_ROOM_KEYS_PREFERENCE_KEY"
        const val SETTINGS_ENCRYPTION_NEVER_SENT_TO_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_NEVER_SENT_TO_PREFERENCE_KEY"
        const val SETTINGS_SHOW_DEVICES_LIST_PREFERENCE_KEY = "SETTINGS_SHOW_DEVICES_LIST_PREFERENCE_KEY"
        const val SETTINGS_ALLOW_INTEGRATIONS_KEY = "SETTINGS_ALLOW_INTEGRATIONS_KEY"
        const val SETTINGS_INTEGRATION_MANAGER_UI_URL_KEY = "SETTINGS_INTEGRATION_MANAGER_UI_URL_KEY"
        const val SETTINGS_SECURE_MESSAGE_RECOVERY_PREFERENCE_KEY = "SETTINGS_SECURE_MESSAGE_RECOVERY_PREFERENCE_KEY"

        const val SETTINGS_CRYPTOGRAPHY_HS_ADMIN_DISABLED_E2E_DEFAULT = "SETTINGS_CRYPTOGRAPHY_HS_ADMIN_DISABLED_E2E_DEFAULT"

        
        const val SETTINGS_PROFILE_PICTURE_PREFERENCE_KEY = "SETTINGS_PROFILE_PICTURE_PREFERENCE_KEY"

        
        const val SETTINGS_CONTACTS_PHONEBOOK_COUNTRY_PREFERENCE_KEY = "SETTINGS_CONTACTS_PHONEBOOK_COUNTRY_PREFERENCE_KEY"

        
        const val SETTINGS_INTERFACE_LANGUAGE_PREFERENCE_KEY = "SETTINGS_INTERFACE_LANGUAGE_PREFERENCE_KEY"
        const val SETTINGS_INTERFACE_TEXT_SIZE_KEY = "SETTINGS_INTERFACE_TEXT_SIZE_KEY"
        const val SETTINGS_INTERFACE_BUBBLE_KEY = "SETTINGS_INTERFACE_BUBBLE_KEY"
        const val SETTINGS_SHOW_URL_PREVIEW_KEY = "SETTINGS_SHOW_URL_PREVIEW_KEY"
        private const val SETTINGS_SEND_TYPING_NOTIF_KEY = "SETTINGS_SEND_TYPING_NOTIF_KEY"
        private const val SETTINGS_ENABLE_MARKDOWN_KEY = "SETTINGS_ENABLE_MARKDOWN_KEY"
        private const val SETTINGS_ALWAYS_SHOW_TIMESTAMPS_KEY = "SETTINGS_ALWAYS_SHOW_TIMESTAMPS_KEY"
        private const val SETTINGS_12_24_TIMESTAMPS_KEY = "SETTINGS_12_24_TIMESTAMPS_KEY"
        private const val SETTINGS_SHOW_READ_RECEIPTS_KEY = "SETTINGS_SHOW_READ_RECEIPTS_KEY"
        private const val SETTINGS_SHOW_REDACTED_KEY = "SETTINGS_SHOW_REDACTED_KEY"
        private const val SETTINGS_SHOW_ROOM_MEMBER_STATE_EVENTS_KEY = "SETTINGS_SHOW_ROOM_MEMBER_STATE_EVENTS_KEY"
        private const val SETTINGS_SHOW_JOIN_LEAVE_MESSAGES_KEY = "SETTINGS_SHOW_JOIN_LEAVE_MESSAGES_KEY"
        private const val SETTINGS_SHOW_AVATAR_DISPLAY_NAME_CHANGES_MESSAGES_KEY = "SETTINGS_SHOW_AVATAR_DISPLAY_NAME_CHANGES_MESSAGES_KEY"
        private const val SETTINGS_VIBRATE_ON_MENTION_KEY = "SETTINGS_VIBRATE_ON_MENTION_KEY"
        private const val SETTINGS_SEND_MESSAGE_WITH_ENTER = "SETTINGS_SEND_MESSAGE_WITH_ENTER"
        private const val SETTINGS_ENABLE_CHAT_EFFECTS = "SETTINGS_ENABLE_CHAT_EFFECTS"
        private const val SETTINGS_SHOW_EMOJI_KEYBOARD = "SETTINGS_SHOW_EMOJI_KEYBOARD"
        private const val SETTINGS_LABS_ENABLE_LATEX_MATHS = "SETTINGS_LABS_ENABLE_LATEX_MATHS"
        const val SETTINGS_PRESENCE_USER_ALWAYS_APPEARS_OFFLINE = "SETTINGS_PRESENCE_USER_ALWAYS_APPEARS_OFFLINE"

        
        private const val SETTINGS_ROOM_DIRECTORY_SHOW_ALL_PUBLIC_ROOMS = "SETTINGS_ROOM_DIRECTORY_SHOW_ALL_PUBLIC_ROOMS"

        
        private const val SETTINGS_SHOULD_SHOW_HELP_ON_ROOM_LIST_KEY = "SETTINGS_SHOULD_SHOW_HELP_ON_ROOM_LIST_KEY"

        
        private const val SETTINGS_PIN_UNREAD_MESSAGES_PREFERENCE_KEY = "SETTINGS_PIN_UNREAD_MESSAGES_PREFERENCE_KEY"
        private const val SETTINGS_PIN_MISSED_NOTIFICATIONS_PREFERENCE_KEY = "SETTINGS_PIN_MISSED_NOTIFICATIONS_PREFERENCE_KEY"

        
        const val SETTINGS_ENABLE_ALL_NOTIF_PREFERENCE_KEY = "SETTINGS_ENABLE_ALL_NOTIF_PREFERENCE_KEY"
        const val SETTINGS_ENABLE_THIS_DEVICE_PREFERENCE_KEY = "SETTINGS_ENABLE_THIS_DEVICE_PREFERENCE_KEY"
        const val SETTINGS_EMAIL_NOTIFICATION_CATEGORY_PREFERENCE_KEY = "SETTINGS_EMAIL_NOTIFICATION_CATEGORY_PREFERENCE_KEY"

        
        const val SETTINGS_SYSTEM_CALL_NOTIFICATION_PREFERENCE_KEY = "SETTINGS_SYSTEM_CALL_NOTIFICATION_PREFERENCE_KEY"
        const val SETTINGS_SYSTEM_NOISY_NOTIFICATION_PREFERENCE_KEY = "SETTINGS_SYSTEM_NOISY_NOTIFICATION_PREFERENCE_KEY"
        const val SETTINGS_SYSTEM_SILENT_NOTIFICATION_PREFERENCE_KEY = "SETTINGS_SYSTEM_SILENT_NOTIFICATION_PREFERENCE_KEY"
        const val SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY = "SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY"
        const val SETTINGS_NOTIFICATION_RINGTONE_SELECTION_PREFERENCE_KEY = "SETTINGS_NOTIFICATION_RINGTONE_SELECTION_PREFERENCE_KEY"

        
        private const val SETTINGS_DEFAULT_MEDIA_COMPRESSION_KEY = "SETTINGS_DEFAULT_MEDIA_COMPRESSION_KEY"
        private const val SETTINGS_DEFAULT_MEDIA_SOURCE_KEY = "SETTINGS_DEFAULT_MEDIA_SOURCE_KEY"
        private const val SETTINGS_PREVIEW_MEDIA_BEFORE_SENDING_KEY = "SETTINGS_PREVIEW_MEDIA_BEFORE_SENDING_KEY"
        private const val SETTINGS_PLAY_SHUTTER_SOUND_KEY = "SETTINGS_PLAY_SHUTTER_SOUND_KEY"

        
        const val SETTINGS_START_ON_BOOT_PREFERENCE_KEY = "SETTINGS_START_ON_BOOT_PREFERENCE_KEY"
        const val SETTINGS_ENABLE_BACKGROUND_SYNC_PREFERENCE_KEY = "SETTINGS_ENABLE_BACKGROUND_SYNC_PREFERENCE_KEY"
        const val SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY = "SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY"
        const val SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY = "SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY"

        
        const val SETTINGS_CALL_PREVENT_ACCIDENTAL_CALL_KEY = "SETTINGS_CALL_PREVENT_ACCIDENTAL_CALL_KEY"
        const val SETTINGS_CALL_RINGTONE_USE_RIOT_PREFERENCE_KEY = "SETTINGS_CALL_RINGTONE_USE_RIOT_PREFERENCE_KEY"
        const val SETTINGS_CALL_RINGTONE_URI_PREFERENCE_KEY = "SETTINGS_CALL_RINGTONE_URI_PREFERENCE_KEY"

        
        const val SETTINGS_LAZY_LOADING_PREFERENCE_KEY = "SETTINGS_LAZY_LOADING_PREFERENCE_KEY"
        const val SETTINGS_USER_REFUSED_LAZY_LOADING_PREFERENCE_KEY = "SETTINGS_USER_REFUSED_LAZY_LOADING_PREFERENCE_KEY"
        const val SETTINGS_DATA_SAVE_MODE_PREFERENCE_KEY = "SETTINGS_DATA_SAVE_MODE_PREFERENCE_KEY"
        private const val SETTINGS_USE_JITSI_CONF_PREFERENCE_KEY = "SETTINGS_USE_JITSI_CONF_PREFERENCE_KEY"
        private const val SETTINGS_USE_NATIVE_CAMERA_PREFERENCE_KEY = "SETTINGS_USE_NATIVE_CAMERA_PREFERENCE_KEY"
        private const val SETTINGS_ENABLE_SEND_VOICE_FEATURE_PREFERENCE_KEY = "SETTINGS_ENABLE_SEND_VOICE_FEATURE_PREFERENCE_KEY"

        const val SETTINGS_LABS_ALLOW_EXTENDED_LOGS = "SETTINGS_LABS_ALLOW_EXTENDED_LOGS"
        const val SETTINGS_LABS_SPACES_HOME_AS_ORPHAN = "SETTINGS_LABS_SPACES_HOME_AS_ORPHAN"
        const val SETTINGS_LABS_AUTO_REPORT_UISI = "SETTINGS_LABS_AUTO_REPORT_UISI"
        const val SETTINGS_PREF_SPACE_SHOW_ALL_ROOM_IN_HOME = "SETTINGS_PREF_SPACE_SHOW_ALL_ROOM_IN_HOME"

        private const val SETTINGS_DEVELOPER_MODE_PREFERENCE_KEY = "SETTINGS_DEVELOPER_MODE_PREFERENCE_KEY"
        private const val SETTINGS_LABS_SHOW_HIDDEN_EVENTS_PREFERENCE_KEY = "SETTINGS_LABS_SHOW_HIDDEN_EVENTS_PREFERENCE_KEY"
        private const val SETTINGS_LABS_ENABLE_SWIPE_TO_REPLY = "SETTINGS_LABS_ENABLE_SWIPE_TO_REPLY"
        private const val SETTINGS_DEVELOPER_MODE_FAIL_FAST_PREFERENCE_KEY = "SETTINGS_DEVELOPER_MODE_FAIL_FAST_PREFERENCE_KEY"
        private const val SETTINGS_DEVELOPER_MODE_SHOW_INFO_ON_SCREEN_KEY = "SETTINGS_DEVELOPER_MODE_SHOW_INFO_ON_SCREEN_KEY"

        
        private const val SETTINGS_LABS_SHOW_COMPLETE_HISTORY_IN_ENCRYPTED_ROOM = "SETTINGS_LABS_SHOW_COMPLETE_HISTORY_IN_ENCRYPTED_ROOM"
        const val SETTINGS_LABS_UNREAD_NOTIFICATIONS_AS_TAB = "SETTINGS_LABS_UNREAD_NOTIFICATIONS_AS_TAB"

        
        const val SETTINGS_USE_RAGE_SHAKE_KEY = "SETTINGS_USE_RAGE_SHAKE_KEY"
        const val SETTINGS_RAGE_SHAKE_DETECTION_THRESHOLD_KEY = "SETTINGS_RAGE_SHAKE_DETECTION_THRESHOLD_KEY"

        
        const val SETTINGS_SECURITY_USE_FLAG_SECURE = "SETTINGS_SECURITY_USE_FLAG_SECURE"
        const val SETTINGS_SECURITY_USE_PIN_CODE_FLAG = "SETTINGS_SECURITY_USE_PIN_CODE_FLAG"
        const val SETTINGS_SECURITY_CHANGE_PIN_CODE_FLAG = "SETTINGS_SECURITY_CHANGE_PIN_CODE_FLAG"
        private const val SETTINGS_SECURITY_USE_BIOMETRICS_FLAG = "SETTINGS_SECURITY_USE_BIOMETRICS_FLAG"
        private const val SETTINGS_SECURITY_USE_GRACE_PERIOD_FLAG = "SETTINGS_SECURITY_USE_GRACE_PERIOD_FLAG"
        const val SETTINGS_SECURITY_USE_COMPLETE_NOTIFICATIONS_FLAG = "SETTINGS_SECURITY_USE_COMPLETE_NOTIFICATIONS_FLAG"

        
        const val SETTINGS_MEDIA_SAVING_PERIOD_KEY = "SETTINGS_MEDIA_SAVING_PERIOD_KEY"
        private const val SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY = "SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY"
        private const val DID_ASK_TO_IGNORE_BATTERY_OPTIMIZATIONS_KEY = "DID_ASK_TO_IGNORE_BATTERY_OPTIMIZATIONS_KEY"
        private const val DID_MIGRATE_TO_NOTIFICATION_REWORK = "DID_MIGRATE_TO_NOTIFICATION_REWORK"
        private const val DID_ASK_TO_USE_ANALYTICS_TRACKING_KEY = "DID_ASK_TO_USE_ANALYTICS_TRACKING_KEY"
        private const val SETTINGS_DISPLAY_ALL_EVENTS_KEY = "SETTINGS_DISPLAY_ALL_EVENTS_KEY"

        private const val DID_ASK_TO_ENABLE_SESSION_PUSH = "DID_ASK_TO_ENABLE_SESSION_PUSH"

        
        const val SETTINGS_PREF_ENABLE_LOCATION_SHARING = "SETTINGS_PREF_ENABLE_LOCATION_SHARING"

        private const val MEDIA_SAVING_3_DAYS = 0
        private const val MEDIA_SAVING_1_WEEK = 1
        private const val MEDIA_SAVING_1_MONTH = 2
        private const val MEDIA_SAVING_FOREVER = 3

        private const val SETTINGS_UNKNOWN_DEVICE_DISMISSED_LIST = "SETTINGS_UNKNWON_DEVICE_DISMISSED_LIST"

        private const val TAKE_PHOTO_VIDEO_MODE = "TAKE_PHOTO_VIDEO_MODE"

        private const val SETTINGS_LABS_RENDER_LOCATIONS_IN_TIMELINE = "SETTINGS_LABS_RENDER_LOCATIONS_IN_TIMELINE"

        
        const val SETTINGS_LABS_ENABLE_THREAD_MESSAGES_OLD_CLIENTS = "SETTINGS_LABS_ENABLE_THREAD_MESSAGES"

        
        const val SETTINGS_LABS_ENABLE_THREAD_MESSAGES = "SETTINGS_LABS_ENABLE_THREAD_MESSAGES_FINAL"
        const val SETTINGS_THREAD_MESSAGES_SYNCED = "SETTINGS_THREAD_MESSAGES_SYNCED"

        
        const val TAKE_PHOTO_VIDEO_MODE_ALWAYS_ASK = 0
        const val TAKE_PHOTO_VIDEO_MODE_PHOTO = 1
        const val TAKE_PHOTO_VIDEO_MODE_VIDEO = 2

        

        
        private val mKeysToKeepAfterLogout = listOf(
                SETTINGS_DEFAULT_MEDIA_COMPRESSION_KEY,
                SETTINGS_DEFAULT_MEDIA_SOURCE_KEY,
                SETTINGS_PLAY_SHUTTER_SOUND_KEY,

                SETTINGS_SEND_TYPING_NOTIF_KEY,
                SETTINGS_ALWAYS_SHOW_TIMESTAMPS_KEY,
                SETTINGS_12_24_TIMESTAMPS_KEY,
                SETTINGS_SHOW_READ_RECEIPTS_KEY,
                SETTINGS_SHOW_ROOM_MEMBER_STATE_EVENTS_KEY,
                SETTINGS_SHOW_JOIN_LEAVE_MESSAGES_KEY,
                SETTINGS_SHOW_AVATAR_DISPLAY_NAME_CHANGES_MESSAGES_KEY,
                SETTINGS_MEDIA_SAVING_PERIOD_KEY,
                SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY,
                SETTINGS_PREVIEW_MEDIA_BEFORE_SENDING_KEY,
                SETTINGS_SEND_MESSAGE_WITH_ENTER,
                SETTINGS_SHOW_EMOJI_KEYBOARD,

                SETTINGS_PIN_UNREAD_MESSAGES_PREFERENCE_KEY,
                SETTINGS_PIN_MISSED_NOTIFICATIONS_PREFERENCE_KEY,
                
                SETTINGS_DATA_SAVE_MODE_PREFERENCE_KEY,
                SETTINGS_START_ON_BOOT_PREFERENCE_KEY,
                SETTINGS_INTERFACE_TEXT_SIZE_KEY,
                SETTINGS_USE_JITSI_CONF_PREFERENCE_KEY,
                SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY,
                SETTINGS_NOTIFICATION_RINGTONE_SELECTION_PREFERENCE_KEY,

                SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_PREFERENCE_KEY,
                SETTINGS_CONTACTS_PHONEBOOK_COUNTRY_PREFERENCE_KEY,
                SETTINGS_INTERFACE_LANGUAGE_PREFERENCE_KEY,
                SETTINGS_BACKGROUND_SYNC_PREFERENCE_KEY,
                SETTINGS_ENABLE_BACKGROUND_SYNC_PREFERENCE_KEY,
                SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY,
                SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY,

                SETTINGS_DEVELOPER_MODE_PREFERENCE_KEY,
                SETTINGS_LABS_SHOW_HIDDEN_EVENTS_PREFERENCE_KEY,
                SETTINGS_LABS_ALLOW_EXTENDED_LOGS,
                SETTINGS_DEVELOPER_MODE_FAIL_FAST_PREFERENCE_KEY,

                SETTINGS_USE_RAGE_SHAKE_KEY,
                SETTINGS_SECURITY_USE_FLAG_SECURE
        )
    }

    private val defaultPrefs = DefaultSharedPreferences.getInstance(context)

    
    fun subscribeToChanges(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        defaultPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unsubscribeToChanges(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        defaultPrefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    
    fun clearPreferences() {
        val keysToKeep = HashSet(mKeysToKeepAfterLogout)

        
        keysToKeep.add(ServerUrlsRepository.HOME_SERVER_URL_PREF)
        keysToKeep.add(ServerUrlsRepository.IDENTITY_SERVER_URL_PREF)

        
        keysToKeep.add(ThemeUtils.APPLICATION_THEME_KEY)

        
        keysToKeep.add(SHARED_PREF_KEY)

        
        val keys = defaultPrefs.all.keys

        
        keys.removeAll(keysToKeep)

        defaultPrefs.edit {
            for (key in keys) {
                remove(key)
            }
        }
    }

    private fun getDefault(@BoolRes resId: Int) = context.resources.getBoolean(resId)

    fun areNotificationEnabledForDevice(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_ENABLE_THIS_DEVICE_PREFERENCE_KEY, true)
    }

    fun setNotificationEnabledForDevice(enabled: Boolean) {
        defaultPrefs.edit {
            putBoolean(SETTINGS_ENABLE_THIS_DEVICE_PREFERENCE_KEY, enabled)
        }
    }

    fun developerMode(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_DEVELOPER_MODE_PREFERENCE_KEY, false)
    }

    fun developerShowDebugInfo(): Boolean {
        return developerMode() && defaultPrefs.getBoolean(SETTINGS_DEVELOPER_MODE_SHOW_INFO_ON_SCREEN_KEY, false)
    }

    fun shouldShowHiddenEvents(): Boolean {
        return developerMode() && defaultPrefs.getBoolean(SETTINGS_LABS_SHOW_HIDDEN_EVENTS_PREFERENCE_KEY, false)
    }

    fun swipeToReplyIsEnabled(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_LABS_ENABLE_SWIPE_TO_REPLY, true)
    }

    fun labShowCompleteHistoryInEncryptedRoom(): Boolean {
        return developerMode() && defaultPrefs.getBoolean(SETTINGS_LABS_SHOW_COMPLETE_HISTORY_IN_ENCRYPTED_ROOM, false)
    }

    fun labAllowedExtendedLogging(): Boolean {
        return developerMode() && defaultPrefs.getBoolean(SETTINGS_LABS_ALLOW_EXTENDED_LOGS, false)
    }

    fun labAddNotificationTab(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_LABS_UNREAD_NOTIFICATIONS_AS_TAB, false)
    }

    fun latexMathsIsEnabled(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_LABS_ENABLE_LATEX_MATHS, false)
    }

    fun failFast(): Boolean {
        return BuildConfig.DEBUG || (developerMode() && defaultPrefs.getBoolean(SETTINGS_DEVELOPER_MODE_FAIL_FAST_PREFERENCE_KEY, false))
    }

    fun didAskUserToEnableSessionPush(): Boolean {
        return defaultPrefs.getBoolean(DID_ASK_TO_ENABLE_SESSION_PUSH, false)
    }

    fun setDidAskUserToEnableSessionPush() {
        defaultPrefs.edit {
            putBoolean(DID_ASK_TO_ENABLE_SESSION_PUSH, true)
        }
    }

    
    fun didAskUserToIgnoreBatteryOptimizations(): Boolean {
        return defaultPrefs.getBoolean(DID_ASK_TO_IGNORE_BATTERY_OPTIMIZATIONS_KEY, false)
    }

    
    fun setDidAskUserToIgnoreBatteryOptimizations() {
        defaultPrefs.edit {
            putBoolean(DID_ASK_TO_IGNORE_BATTERY_OPTIMIZATIONS_KEY, true)
        }
    }

    fun didMigrateToNotificationRework(): Boolean {
        return defaultPrefs.getBoolean(DID_MIGRATE_TO_NOTIFICATION_REWORK, false)
    }

    fun setDidMigrateToNotificationRework() {
        defaultPrefs.edit {
            putBoolean(DID_MIGRATE_TO_NOTIFICATION_REWORK, true)
        }
    }

    
    fun displayTimeIn12hFormat(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_12_24_TIMESTAMPS_KEY, false)
    }

    
    fun showJoinLeaveMessages(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SHOW_JOIN_LEAVE_MESSAGES_KEY, true)
    }

    
    fun showAvatarDisplayNameChangeMessages(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SHOW_AVATAR_DISPLAY_NAME_CHANGES_MESSAGES_KEY, true)
    }

    
    fun useNativeCamera(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_USE_NATIVE_CAMERA_PREFERENCE_KEY, false)
    }

    
    fun isSendVoiceFeatureEnabled(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_ENABLE_SEND_VOICE_FEATURE_PREFERENCE_KEY, false)
    }

    
    fun showAllPublicRooms(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_ROOM_DIRECTORY_SHOW_ALL_PUBLIC_ROOMS, false)
    }

    
    fun getSelectedDefaultMediaCompressionLevel(): Int {
        return Integer.parseInt(defaultPrefs.getString(SETTINGS_DEFAULT_MEDIA_COMPRESSION_KEY, "0")!!)
    }

    
    fun getSelectedDefaultMediaSource(): Int {
        return Integer.parseInt(defaultPrefs.getString(SETTINGS_DEFAULT_MEDIA_SOURCE_KEY, "0")!!)
    }

    
    fun useShutterSound(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_PLAY_SHUTTER_SOUND_KEY, true)
    }

    fun storeUnknownDeviceDismissedList(deviceIds: List<String>) {
        defaultPrefs.edit(true) {
            putStringSet(SETTINGS_UNKNOWN_DEVICE_DISMISSED_LIST, deviceIds.toSet())
        }
    }

    fun getUnknownDeviceDismissedList(): List<String> {
        return tryOrNull {
            defaultPrefs.getStringSet(SETTINGS_UNKNOWN_DEVICE_DISMISSED_LIST, null)?.toList()
        }.orEmpty()
    }

    
    fun setNotificationRingTone(uri: Uri?) {
        defaultPrefs.edit {
            var value = ""

            if (null != uri) {
                value = uri.toString()

                if (value.startsWith("file://")) {
                    
                    
                    
                    return
                }
            }

            putString(SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY, value)
        }
    }

    
    fun getNotificationRingTone(): Uri? {
        val url = defaultPrefs.getString(SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY, null)

        
        if (url == "") {
            return null
        }

        var uri: Uri? = null

        
        if (null != url && !url.startsWith("file://")) {
            try {
                uri = Uri.parse(url)
            } catch (e: Exception) {
                Timber.e(e, "## getNotificationRingTone() : Uri.parse failed")
            }
        }

        if (null == uri) {
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        Timber.v("## getNotificationRingTone() returns $uri")
        return uri
    }

    
    fun getNotificationRingToneName(): String? {
        val toneUri = getNotificationRingTone() ?: return null

        try {
            val proj = arrayOf(MediaStore.Audio.Media.DISPLAY_NAME)
            return context.contentResolver.query(toneUri, proj, null, null, null)?.use {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                it.moveToFirst()
                it.getString(columnIndex)
            }
        } catch (e: Exception) {
            Timber.e(e, "## getNotificationRingToneName() failed")
        }

        return null
    }

    
    fun setUseLazyLoading(newValue: Boolean) {
        defaultPrefs.edit {
            putBoolean(SETTINGS_LAZY_LOADING_PREFERENCE_KEY, newValue)
        }
    }

    
    fun useLazyLoading(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_LAZY_LOADING_PREFERENCE_KEY, false)
    }

    
    fun setUserRefuseLazyLoading() {
        defaultPrefs.edit {
            putBoolean(SETTINGS_USER_REFUSED_LAZY_LOADING_PREFERENCE_KEY, true)
        }
    }

    
    fun hasUserRefusedLazyLoading(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_USER_REFUSED_LAZY_LOADING_PREFERENCE_KEY, false)
    }

    
    fun useDataSaveMode(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_DATA_SAVE_MODE_PREFERENCE_KEY, false)
    }

    
    fun useJitsiConfCall(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_USE_JITSI_CONF_PREFERENCE_KEY, true)
    }

    
    fun autoStartOnBoot(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_START_ON_BOOT_PREFERENCE_KEY, true)
    }

    
    fun setAutoStartOnBoot(value: Boolean) {
        defaultPrefs.edit {
            putBoolean(SETTINGS_START_ON_BOOT_PREFERENCE_KEY, value)
        }
    }

    
    fun getSelectedMediasSavingPeriod(): Int {
        return defaultPrefs.getInt(SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY, MEDIA_SAVING_1_WEEK)
    }

    
    fun setSelectedMediasSavingPeriod(index: Int) {
        defaultPrefs.edit {
            putInt(SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY, index)
        }
    }

    
    fun getMinMediasLastAccessTime(): Long {
        return when (getSelectedMediasSavingPeriod()) {
            MEDIA_SAVING_3_DAYS  -> System.currentTimeMillis() / 1000 - 3 * 24 * 60 * 60
            MEDIA_SAVING_1_WEEK  -> System.currentTimeMillis() / 1000 - 7 * 24 * 60 * 60
            MEDIA_SAVING_1_MONTH -> System.currentTimeMillis() / 1000 - 30 * 24 * 60 * 60
            MEDIA_SAVING_FOREVER -> 0
            else                 -> 0
        }
    }

    
    fun getSelectedMediasSavingPeriodString(): String {
        return when (getSelectedMediasSavingPeriod()) {
            MEDIA_SAVING_3_DAYS  -> context.getString(R.string.media_saving_period_3_days)
            MEDIA_SAVING_1_WEEK  -> context.getString(R.string.media_saving_period_1_week)
            MEDIA_SAVING_1_MONTH -> context.getString(R.string.media_saving_period_1_month)
            MEDIA_SAVING_FOREVER -> context.getString(R.string.media_saving_period_forever)
            else                 -> "?"
        }
    }

    
    fun fixMigrationIssues() {
        
    }

    
    fun isMarkdownEnabled(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_ENABLE_MARKDOWN_KEY, false)
    }

    
    fun setMarkdownEnabled(isEnabled: Boolean) {
        defaultPrefs.edit {
            putBoolean(SETTINGS_ENABLE_MARKDOWN_KEY, isEnabled)
        }
    }

    
    fun preventAccidentalCall(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_CALL_PREVENT_ACCIDENTAL_CALL_KEY, false)
    }

    
    fun showReadReceipts(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SHOW_READ_RECEIPTS_KEY, true)
    }

    
    fun showRedactedMessages(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SHOW_REDACTED_KEY, true)
    }

    
    fun shouldShowLongClickOnRoomHelp(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SHOULD_SHOW_HELP_ON_ROOM_LIST_KEY, true)
    }

    
    fun neverShowLongClickOnRoomHelpAgain() {
        defaultPrefs.edit {
            putBoolean(SETTINGS_SHOULD_SHOW_HELP_ON_ROOM_LIST_KEY, false)
        }
    }

    
    fun alwaysShowTimeStamps(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_ALWAYS_SHOW_TIMESTAMPS_KEY, false)
    }

    
    fun sendTypingNotifs(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SEND_TYPING_NOTIF_KEY, true)
    }

    
    fun pinMissedNotifications(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_PIN_MISSED_NOTIFICATIONS_PREFERENCE_KEY, true)
    }

    
    fun pinUnreadMessages(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_PIN_UNREAD_MESSAGES_PREFERENCE_KEY, true)
    }

    
    fun vibrateWhenMentioning(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_VIBRATE_ON_MENTION_KEY, false)
    }

    
    fun didAskToUseAnalytics(): Boolean {
        return defaultPrefs.getBoolean(DID_ASK_TO_USE_ANALYTICS_TRACKING_KEY, false)
    }

    
    fun setDidAskToUseAnalytics() {
        defaultPrefs.edit {
            putBoolean(DID_ASK_TO_USE_ANALYTICS_TRACKING_KEY, true)
        }
    }

    
    fun showUrlPreviews(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SHOW_URL_PREVIEW_KEY, true)
    }

    
    fun previewMediaWhenSending(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_PREVIEW_MEDIA_BEFORE_SENDING_KEY, false)
    }

    
    fun sendMessageWithEnter(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SEND_MESSAGE_WITH_ENTER, false)
    }

    
    fun showEmojiKeyboard(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SHOW_EMOJI_KEYBOARD, true)
    }

    
    fun useMessageBubblesLayout(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_INTERFACE_BUBBLE_KEY, getDefault(R.bool.settings_interface_bubble_default))
    }

    
    fun userAlwaysAppearsOffline(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_PRESENCE_USER_ALWAYS_APPEARS_OFFLINE,
                getDefault(R.bool.settings_presence_user_always_appears_offline_default))
    }

    
    fun setRageshakeEnabled(isEnabled: Boolean) {
        defaultPrefs.edit {
            putBoolean(SETTINGS_USE_RAGE_SHAKE_KEY, isEnabled)
        }
    }

    
    fun useRageshake(): Boolean {
        return false
    }

    
    fun getRageshakeSensitivity(): Int {
        return defaultPrefs.getInt(SETTINGS_RAGE_SHAKE_DETECTION_THRESHOLD_KEY, ShakeDetector.SENSITIVITY_MEDIUM)
    }

    
    fun displayAllEvents(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_DISPLAY_ALL_EVENTS_KEY, false)
    }

    
    fun useFlagSecure(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SECURITY_USE_FLAG_SECURE, false)
    }

    
    fun useFlagPinCode(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SECURITY_USE_PIN_CODE_FLAG, false)
    }

    fun useBiometricsToUnlock(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SECURITY_USE_BIOMETRICS_FLAG, true)
    }

    fun useGracePeriod(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_SECURITY_USE_GRACE_PERIOD_FLAG, true)
    }

    fun chatEffectsEnabled(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_ENABLE_CHAT_EFFECTS, true)
    }

    
    fun useCompleteNotificationFormat(): Boolean {
        return !useFlagPinCode() ||
                defaultPrefs.getBoolean(SETTINGS_SECURITY_USE_COMPLETE_NOTIFICATIONS_FLAG, true)
    }

    fun backgroundSyncTimeOut(): Int {
        return tryOrNull {
            
            defaultPrefs.getString(SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY, null)?.toInt()
        } ?: BackgroundSyncMode.DEFAULT_SYNC_TIMEOUT_SECONDS
    }

    fun setBackgroundSyncTimeout(timeInSecond: Int) {
        defaultPrefs
                .edit()
                .putString(SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY, timeInSecond.toString())
                .apply()
    }

    fun backgroundSyncDelay(): Int {
        return tryOrNull {
            
            defaultPrefs.getString(SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY, null)?.toInt()
        } ?: BackgroundSyncMode.DEFAULT_SYNC_DELAY_SECONDS
    }

    fun setBackgroundSyncDelay(timeInSecond: Int) {
        defaultPrefs
                .edit()
                .putString(SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY, timeInSecond.toString())
                .apply()
    }

    fun isBackgroundSyncEnabled(): Boolean {
        return getFdroidSyncBackgroundMode() != BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_DISABLED
    }

    fun setFdroidSyncBackgroundMode(mode: BackgroundSyncMode) {
        defaultPrefs
                .edit()
                .putString(SETTINGS_FDROID_BACKGROUND_SYNC_MODE, mode.name)
                .apply()
    }

    fun getFdroidSyncBackgroundMode(): BackgroundSyncMode {
        return try {
            val strPref = defaultPrefs
                    .getString(SETTINGS_FDROID_BACKGROUND_SYNC_MODE, BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_BATTERY.name)
            BackgroundSyncMode.values().firstOrNull { it.name == strPref } ?: BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_BATTERY
        } catch (e: Throwable) {
            BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_BATTERY
        }
    }

    private fun labsSpacesOnlyOrphansInHome(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_LABS_SPACES_HOME_AS_ORPHAN, false)
    }

    fun labsAutoReportUISI(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_LABS_AUTO_REPORT_UISI, false)
    }

    fun prefSpacesShowAllRoomInHome(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_PREF_SPACE_SHOW_ALL_ROOM_IN_HOME,
                
                !labsSpacesOnlyOrphansInHome())
    }

    
    fun getTakePhotoVideoMode(): Int {
        return defaultPrefs.getInt(TAKE_PHOTO_VIDEO_MODE, TAKE_PHOTO_VIDEO_MODE_ALWAYS_ASK)
    }

    fun setTakePhotoVideoMode(mode: Int) {
        return defaultPrefs.edit {
            putInt(TAKE_PHOTO_VIDEO_MODE, mode)
        }
    }

    fun isLocationSharingEnabled(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_PREF_ENABLE_LOCATION_SHARING, false) && BuildConfig.enableLocationSharing
    }

    fun labsRenderLocationsInTimeline(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_LABS_RENDER_LOCATIONS_IN_TIMELINE, true)
    }

    
    fun areThreadMessagesEnabled(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_LABS_ENABLE_THREAD_MESSAGES, getDefault(R.bool.settings_labs_thread_messages_default))
    }

    
    fun setThreadMessagesEnabled() {
        defaultPrefs
                .edit()
                .putBoolean(SETTINGS_LABS_ENABLE_THREAD_MESSAGES, true)
                .apply()
    }

    
    fun shouldNotifyUserAboutThreads(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_LABS_ENABLE_THREAD_MESSAGES_OLD_CLIENTS, false)
    }

    
    fun userNotifiedAboutThreads() {
        defaultPrefs
                .edit()
                .putBoolean(SETTINGS_LABS_ENABLE_THREAD_MESSAGES_OLD_CLIENTS, false)
                .apply()
    }

    
    fun shouldMigrateThreads(): Boolean {
        return defaultPrefs.getBoolean(SETTINGS_THREAD_MESSAGES_SYNCED, true)
    }

    
    fun setShouldMigrateThreads(shouldMigrate: Boolean) {
        defaultPrefs
                .edit()
                .putBoolean(SETTINGS_THREAD_MESSAGES_SYNCED, shouldMigrate)
                .apply()
    }
}
