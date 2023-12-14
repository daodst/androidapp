

package org.matrix.android.sdk.internal.session.filter

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.sync.FilterService
import org.matrix.android.sdk.internal.session.SessionScope
import retrofit2.Retrofit

@Module
internal abstract class FilterModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesFilterApi(retrofit: Retrofit): FilterApi {
            return retrofit.create(FilterApi::class.java)
        }
    }

    @Binds
    abstract fun bindFilterRepository(repository: DefaultFilterRepository): FilterRepository

    @Binds
    abstract fun bindFilterService(service: DefaultFilterService): FilterService

    @Binds
    abstract fun bindSaveFilterTask(task: DefaultSaveFilterTask): SaveFilterTask
}
