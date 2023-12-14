

package org.matrix.android.sdk.internal.session.presence.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.presence.PresenceService
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.session.presence.PresenceAPI
import org.matrix.android.sdk.internal.session.presence.service.DefaultPresenceService
import org.matrix.android.sdk.internal.session.presence.service.task.DefaultGetPresenceTask
import org.matrix.android.sdk.internal.session.presence.service.task.DefaultSetPresenceTask
import org.matrix.android.sdk.internal.session.presence.service.task.GetPresenceTask
import org.matrix.android.sdk.internal.session.presence.service.task.SetPresenceTask
import retrofit2.Retrofit

@Module
internal abstract class PresenceModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesPresenceAPI(retrofit: Retrofit): PresenceAPI {
            return retrofit.create(PresenceAPI::class.java)
        }
    }

    @Binds
    abstract fun bindPresenceService(service: DefaultPresenceService): PresenceService

    @Binds
    abstract fun bindSetPresenceTask(task: DefaultSetPresenceTask): SetPresenceTask

    @Binds
    abstract fun bindGetPresenceTask(task: DefaultGetPresenceTask): GetPresenceTask
}
