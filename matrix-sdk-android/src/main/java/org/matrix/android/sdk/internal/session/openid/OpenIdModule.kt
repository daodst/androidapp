

package org.matrix.android.sdk.internal.session.openid

import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
internal abstract class OpenIdModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun providesOpenIdAPI(retrofit: Retrofit): OpenIdAPI {
            return retrofit.create(OpenIdAPI::class.java)
        }
    }

    @Binds
    abstract fun bindGetOpenIdTokenTask(task: DefaultGetOpenIdTokenTask): GetOpenIdTokenTask
}
