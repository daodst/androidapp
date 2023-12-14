

package org.matrix.android.sdk.internal.settings

import dagger.Binds
import dagger.Module
import org.matrix.android.sdk.api.settings.LightweightSettingsStorage

@Module
internal abstract class SettingsModule {
    @Binds
    abstract fun bindLightweightSettingsStorage(storage: DefaultLightweightSettingsStorage): LightweightSettingsStorage
}
