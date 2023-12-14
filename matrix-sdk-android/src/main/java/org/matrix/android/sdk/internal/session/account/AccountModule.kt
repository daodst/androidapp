

package org.matrix.android.sdk.internal.session.account

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.account.AccountService
import org.matrix.android.sdk.internal.session.SessionScope
import retrofit2.Retrofit

@Module
internal abstract class AccountModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesAccountAPI(retrofit: Retrofit): AccountAPI {
            return retrofit.create(AccountAPI::class.java)
        }
    }

    @Binds
    abstract fun bindChangePasswordTask(task: DefaultChangePasswordTask): ChangePasswordTask

    @Binds
    abstract fun bindDeactivateAccountTask(task: DefaultDeactivateAccountTask): DeactivateAccountTask

    @Binds
    abstract fun bindAccountService(service: DefaultAccountService): AccountService
}
