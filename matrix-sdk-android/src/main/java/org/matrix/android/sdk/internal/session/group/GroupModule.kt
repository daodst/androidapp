

package org.matrix.android.sdk.internal.session.group

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.group.GroupService
import org.matrix.android.sdk.internal.session.SessionScope
import retrofit2.Retrofit

@Module
internal abstract class GroupModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesGroupAPI(retrofit: Retrofit): GroupAPI {
            return retrofit.create(GroupAPI::class.java)
        }
    }

    @Binds
    abstract fun bindGroupFactory(factory: DefaultGroupFactory): GroupFactory

    @Binds
    abstract fun bindGetGroupDataTask(task: DefaultGetGroupDataTask): GetGroupDataTask

    @Binds
    abstract fun bindGroupService(service: DefaultGroupService): GroupService
}
