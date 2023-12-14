

package im.vector.app.features.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import im.vector.app.core.extensions.dataStoreProvider
import im.vector.app.features.onboarding.FtueUseCase
import kotlinx.coroutines.flow.first
import org.matrix.android.sdk.api.util.md5


class VectorSessionStore constructor(
        context: Context,
        myUserId: String
) {

    private val useCaseKey = stringPreferencesKey("use_case")
    private val dataStore by lazy { context.dataStoreProvider("vector_session_store_${myUserId.md5()}") }

    suspend fun readUseCase() = dataStore.data.first().let { preferences ->
        preferences[useCaseKey]?.let { FtueUseCase.from(it) }
    }

    suspend fun setUseCase(useCase: FtueUseCase) {
        dataStore.edit { settings ->
            settings[useCaseKey] = useCase.persistableValue
        }
    }

    suspend fun resetUseCase() {
        dataStore.edit { settings ->
            settings.remove(useCaseKey)
        }
    }

    suspend fun clear() {
        dataStore.edit { settings -> settings.clear() }
    }
}
