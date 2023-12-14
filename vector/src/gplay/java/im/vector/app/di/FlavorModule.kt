

package im.vector.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import im.vector.app.core.services.GuardServiceStarter

@InstallIn(SingletonComponent::class)
@Module
object FlavorModule {

    @Provides
    fun provideGuardServiceStarter(): GuardServiceStarter {
        return object : GuardServiceStarter {}
    }
}
