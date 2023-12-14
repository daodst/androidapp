

package im.vector.app.features.debug.features

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import im.vector.app.features.HomeserverCapabilitiesOverride
import im.vector.app.features.VectorOverrides
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.matrix.android.sdk.api.extensions.orFalse

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vector_overrides")
private val keyForceDialPadDisplay = booleanPreferencesKey("force_dial_pad_display")
private val keyForceLoginFallback = booleanPreferencesKey("force_login_fallback")
private val forceCanChangeDisplayName = booleanPreferencesKey("force_can_change_display_name")
private val forceCanChangeAvatar = booleanPreferencesKey("force_can_change_avatar")

class DebugVectorOverrides(private val context: Context) : VectorOverrides {

    override val forceDialPad = context.dataStore.data.map { preferences ->
        preferences[keyForceDialPadDisplay].orFalse()
    }

    override val forceLoginFallback = context.dataStore.data.map { preferences ->
        preferences[keyForceLoginFallback].orFalse()
    }

    override val forceHomeserverCapabilities = context.dataStore.data.map { preferences ->
        HomeserverCapabilitiesOverride(
                canChangeDisplayName = preferences[forceCanChangeDisplayName],
                canChangeAvatar = preferences[forceCanChangeAvatar]
        )
    }

    suspend fun setForceDialPadDisplay(force: Boolean) {
        context.dataStore.edit { settings ->
            settings[keyForceDialPadDisplay] = force
        }
    }

    suspend fun setForceLoginFallback(force: Boolean) {
        context.dataStore.edit { settings ->
            settings[keyForceLoginFallback] = force
        }
    }

    suspend fun setHomeserverCapabilities(block: HomeserverCapabilitiesOverride.() -> HomeserverCapabilitiesOverride) {
        val capabilitiesOverride = block(forceHomeserverCapabilities.firstOrNull() ?: HomeserverCapabilitiesOverride(null, null))
        context.dataStore.edit { settings ->
            when (capabilitiesOverride.canChangeDisplayName) {
                null -> settings.remove(forceCanChangeDisplayName)
                else -> settings[forceCanChangeDisplayName] = capabilitiesOverride.canChangeDisplayName
            }
            when (capabilitiesOverride.canChangeAvatar) {
                null -> settings.remove(forceCanChangeAvatar)
                else -> settings[forceCanChangeAvatar] = capabilitiesOverride.canChangeAvatar
            }
        }
    }
}
