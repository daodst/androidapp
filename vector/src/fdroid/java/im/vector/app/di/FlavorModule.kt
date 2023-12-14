

package im.vector.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import im.vector.app.core.services.GuardServiceStarter
import im.vector.app.fdroid.service.FDroidGuardServiceStarter
import im.vector.app.features.settings.VectorPreferences

@InstallIn(SingletonComponent::class)
@Module
object FlavorModule {

    @Provides
    fun provideGuardServiceStarter(preferences: VectorPreferences, appContext: Context): GuardServiceStarter {
        return FDroidGuardServiceStarter(preferences, appContext)
    }
}
