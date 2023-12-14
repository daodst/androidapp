

package org.matrix.android.sdk.internal.session.user.accountdata

import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
internal abstract class AccountDataModule {

    @Module
    companion object {

        @JvmStatic
        @Provides
        fun providesAccountDataAPI(retrofit: Retrofit): AccountDataAPI {
            return retrofit.create(AccountDataAPI::class.java)
        }
    }

    @Binds
    abstract fun bindUpdateUserAccountDataTask(task: DefaultUpdateUserAccountDataTask): UpdateUserAccountDataTask

    @Binds
    abstract fun bindSaveBreadcrumbsTask(task: DefaultSaveBreadcrumbsTask): SaveBreadcrumbsTask

    @Binds
    abstract fun bindUpdateBreadcrumbsTask(task: DefaultUpdateBreadcrumbsTask): UpdateBreadcrumbsTask
}
