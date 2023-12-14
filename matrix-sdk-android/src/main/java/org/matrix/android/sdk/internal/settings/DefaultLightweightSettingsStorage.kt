

package org.matrix.android.sdk.internal.settings

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.settings.LightweightSettingsStorage
import org.matrix.android.sdk.internal.session.sync.SyncPresence
import javax.inject.Inject


internal class DefaultLightweightSettingsStorage @Inject constructor(
        context: Context,
        private val matrixConfiguration: MatrixConfiguration
) : LightweightSettingsStorage {

    private val sdkDefaultPrefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    override fun setThreadMessagesEnabled(enabled: Boolean) {
        sdkDefaultPrefs.edit {
            putBoolean(MATRIX_SDK_SETTINGS_THREAD_MESSAGES_ENABLED, enabled)
        }
    }

    override fun areThreadMessagesEnabled(): Boolean {
        return sdkDefaultPrefs.getBoolean(MATRIX_SDK_SETTINGS_THREAD_MESSAGES_ENABLED, matrixConfiguration.threadMessagesEnabledDefault)
    }

    
    internal fun setSyncPresenceStatus(presence: SyncPresence) {
        sdkDefaultPrefs.edit {
            putString(MATRIX_SDK_SETTINGS_FOREGROUND_PRESENCE_STATUS, presence.value)
        }
    }

    
    internal fun getSyncPresenceStatus(): SyncPresence {
        val presenceString = sdkDefaultPrefs.getString(MATRIX_SDK_SETTINGS_FOREGROUND_PRESENCE_STATUS, SyncPresence.Online.value)
        return SyncPresence.from(presenceString) ?: SyncPresence.Online
    }

    companion object {
        const val MATRIX_SDK_SETTINGS_THREAD_MESSAGES_ENABLED = "MATRIX_SDK_SETTINGS_THREAD_MESSAGES_ENABLED"
        private const val MATRIX_SDK_SETTINGS_FOREGROUND_PRESENCE_STATUS = "MATRIX_SDK_SETTINGS_FOREGROUND_PRESENCE_STATUS"
    }
}
