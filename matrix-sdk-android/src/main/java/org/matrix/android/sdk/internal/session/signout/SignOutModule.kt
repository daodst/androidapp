

package org.matrix.android.sdk.internal.session.signout

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.signout.SignOutService
import org.matrix.android.sdk.internal.session.SessionScope
import retrofit2.Retrofit

@Module
internal abstract class SignOutModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesSignOutAPI(retrofit: Retrofit): SignOutAPI {
            return retrofit.create(SignOutAPI::class.java)
        }
    }

    @Binds
    abstract fun bindSignOutTask(task: DefaultSignOutTask): SignOutTask

    @Binds
    abstract fun bindSignInAgainTask(task: DefaultSignInAgainTask): SignInAgainTask

    @Binds
    abstract fun bindSignOutService(service: DefaultSignOutService): SignOutService
}
