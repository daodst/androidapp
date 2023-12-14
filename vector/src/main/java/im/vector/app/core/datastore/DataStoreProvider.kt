

package im.vector.app.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


fun dataStoreProvider(): ReadOnlyProperty<Context, (String) -> DataStore<Preferences>> {
    return MappedPreferenceDataStoreSingletonDelegate()
}

private class MappedPreferenceDataStoreSingletonDelegate : ReadOnlyProperty<Context, (String) -> DataStore<Preferences>> {

    private val dataStoreCache = ConcurrentHashMap<String, DataStore<Preferences>>()
    private val provider: (Context) -> (String) -> DataStore<Preferences> = { context ->
        { key ->
            dataStoreCache.getOrPut(key) {
                PreferenceDataStoreFactory.create {
                    context.applicationContext.preferencesDataStoreFile(key)
                }
            }
        }
    }

    override fun getValue(thisRef: Context, property: KProperty<*>) = provider.invoke(thisRef)
}
