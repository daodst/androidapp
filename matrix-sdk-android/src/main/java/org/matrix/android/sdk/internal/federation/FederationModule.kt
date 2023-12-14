

package org.matrix.android.sdk.internal.federation

import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.matrix.android.sdk.api.auth.data.SessionParams
import org.matrix.android.sdk.api.federation.FederationService
import org.matrix.android.sdk.internal.di.Unauthenticated
import org.matrix.android.sdk.internal.network.RetrofitFactory

@Module
internal abstract class FederationModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun providesFederationAPI(@Unauthenticated okHttpClient: Lazy<OkHttpClient>,
                                  sessionParams: SessionParams,
                                  retrofitFactory: RetrofitFactory): FederationAPI {
            return retrofitFactory.create(okHttpClient, sessionParams.homeServerUrlBase)
                    .create(FederationAPI::class.java)
        }
    }

    @Binds
    abstract fun bindFederationService(service: DefaultFederationService): FederationService

    @Binds
    abstract fun bindGetFederationVersionTask(task: DefaultGetFederationVersionTask): GetFederationVersionTask
}
