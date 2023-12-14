

package org.matrix.android.sdk.api.settings

interface LightweightSettingsStorage {
    fun setThreadMessagesEnabled(enabled: Boolean)
    fun areThreadMessagesEnabled(): Boolean
}
