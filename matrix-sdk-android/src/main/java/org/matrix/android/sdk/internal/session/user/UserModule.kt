

package org.matrix.android.sdk.internal.session.user

import dagger.Binds
import dagger.Module
import dagger.Provides
import im.vector.app.provide.log.ChatPhoneLogSource
import org.matrix.android.sdk.api.session.user.UserService
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.session.log.ChatPhoneLogService
import org.matrix.android.sdk.internal.session.user.accountdata.DefaultUpdateIgnoredUserIdsTask
import org.matrix.android.sdk.internal.session.user.accountdata.UpdateIgnoredUserIdsTask
import org.matrix.android.sdk.internal.session.user.model.DefaultSearchUserTask
import org.matrix.android.sdk.internal.session.user.model.SearchUserTask
import retrofit2.Retrofit

@Module
internal abstract class UserModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesSearchUserAPI(retrofit: Retrofit): SearchUserAPI {
            return retrofit.create(SearchUserAPI::class.java)
        }


    }

    @Binds
    abstract fun bindUserService(service: DefaultUserService): UserService


    @Binds
    abstract fun bindChatPhoneLogService(service: ChatPhoneLogSource): ChatPhoneLogService

    @Binds
    abstract fun bindSearchUserTask(task: DefaultSearchUserTask): SearchUserTask

    @Binds
    abstract fun bindUpdateIgnoredUserIdsTask(task: DefaultUpdateIgnoredUserIdsTask): UpdateIgnoredUserIdsTask

    @Binds
    abstract fun bindUserStore(store: RealmUserStore): UserStore
}
