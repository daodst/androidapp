

package im.vector.app.features.analytics.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.matrix.android.sdk.api.extensions.orFalse
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vector_analytics")


class AnalyticsStore @Inject constructor(
        private val context: Context
) {
    private val userConsent = booleanPreferencesKey("user_consent")
    private val didAskUserConsent = booleanPreferencesKey("did_ask_user_consent")
    private val analyticsId = stringPreferencesKey("analytics_id")

    val userConsentFlow: Flow<Boolean> = context.dataStore.data
            .map { preferences -> preferences[userConsent].orFalse() }
            .distinctUntilChanged()

    val didAskUserConsentFlow: Flow<Boolean> = context.dataStore.data
            .map { preferences -> preferences[didAskUserConsent].orFalse() }
            .distinctUntilChanged()

    val analyticsIdFlow: Flow<String> = context.dataStore.data
            .map { preferences -> preferences[analyticsId].orEmpty() }
            .distinctUntilChanged()

    suspend fun setUserConsent(newUserConsent: Boolean) {
        context.dataStore.edit { settings ->
            settings[userConsent] = newUserConsent
        }
    }

    suspend fun setDidAskUserConsent(newValue: Boolean = true) {
        context.dataStore.edit { settings ->
            settings[didAskUserConsent] = newValue
        }
    }

    suspend fun setAnalyticsId(newAnalyticsId: String) {
        context.dataStore.edit { settings ->
            settings[analyticsId] = newAnalyticsId
        }
    }
}
