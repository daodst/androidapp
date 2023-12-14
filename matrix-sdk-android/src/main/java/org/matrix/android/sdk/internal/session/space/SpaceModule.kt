

package org.matrix.android.sdk.internal.session.space

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.session.room.DefaultSpaceGetter
import org.matrix.android.sdk.internal.session.room.SpaceGetter
import org.matrix.android.sdk.internal.session.space.peeking.DefaultPeekSpaceTask
import org.matrix.android.sdk.internal.session.space.peeking.PeekSpaceTask
import retrofit2.Retrofit

@Module
internal abstract class SpaceModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesSpacesAPI(retrofit: Retrofit): SpaceApi {
            return retrofit.create(SpaceApi::class.java)
        }
    }

    @Binds
    abstract fun bindResolveSpaceTask(task: DefaultResolveSpaceInfoTask): ResolveSpaceInfoTask

    @Binds
    abstract fun bindPeekSpaceTask(task: DefaultPeekSpaceTask): PeekSpaceTask

    @Binds
    abstract fun bindJoinSpaceTask(task: DefaultJoinSpaceTask): JoinSpaceTask

    @Binds
    abstract fun bindSpaceGetter(getter: DefaultSpaceGetter): SpaceGetter
}
