

package org.matrix.android.sdk.internal.session.call

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.call.CallSignalingService
import org.matrix.android.sdk.internal.session.SessionScope
import retrofit2.Retrofit

@Module
internal abstract class CallModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesVoipApi(retrofit: Retrofit): VoipApi {
            return retrofit.create(VoipApi::class.java)
        }
    }

    @Binds
    abstract fun bindCallSignalingService(service: DefaultCallSignalingService): CallSignalingService

    @Binds
    abstract fun bindGetTurnServerTask(task: DefaultGetTurnServerTask): GetTurnServerTask
}
