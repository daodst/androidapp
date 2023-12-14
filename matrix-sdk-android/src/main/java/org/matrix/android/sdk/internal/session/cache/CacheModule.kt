

package org.matrix.android.sdk.internal.session.cache

import dagger.Binds
import dagger.Module
import dagger.Provides
import io.realm.RealmConfiguration
import org.matrix.android.sdk.api.session.cache.CacheService
import org.matrix.android.sdk.internal.di.SessionDatabase

@Module
internal abstract class CacheModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @SessionDatabase
        fun providesClearCacheTask(@SessionDatabase realmConfiguration: RealmConfiguration): ClearCacheTask {
            return RealmClearCacheTask(realmConfiguration)
        }
    }

    @Binds
    abstract fun bindCacheService(service: DefaultCacheService): CacheService
}
