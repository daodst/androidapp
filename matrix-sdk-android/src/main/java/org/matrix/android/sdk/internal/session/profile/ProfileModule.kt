

package org.matrix.android.sdk.internal.session.profile

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.profile.ProfileService
import org.matrix.android.sdk.internal.session.SessionScope
import retrofit2.Retrofit

@Module
internal abstract class ProfileModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesProfileAPI(retrofit: Retrofit): ProfileAPI {
            return retrofit.create(ProfileAPI::class.java)
        }
    }

    @Binds
    abstract fun bindProfileService(service: DefaultProfileService): ProfileService

    @Binds
    abstract fun bindGetProfileTask(task: DefaultGetProfileInfoTask): GetProfileInfoTask

    @Binds
    abstract fun bindRefreshUserThreePidsTask(task: DefaultRefreshUserThreePidsTask): RefreshUserThreePidsTask

    @Binds
    abstract fun bindBindThreePidsTask(task: DefaultBindThreePidsTask): BindThreePidsTask

    @Binds
    abstract fun bindUnbindThreePidsTask(task: DefaultUnbindThreePidsTask): UnbindThreePidsTask

    @Binds
    abstract fun bindSetDisplayNameTask(task: DefaultSetDisplayNameTask): SetDisplayNameTask

    @Binds
    abstract fun bindSetAvatarUrlTask(task: DefaultSetAvatarUrlTask): SetAvatarUrlTask

    @Binds
    abstract fun bindAddThreePidTask(task: DefaultAddThreePidTask): AddThreePidTask

    @Binds
    abstract fun bindValidateSmsCodeTask(task: DefaultValidateSmsCodeTask): ValidateSmsCodeTask

    @Binds
    abstract fun bindFinalizeAddingThreePidTask(task: DefaultFinalizeAddingThreePidTask): FinalizeAddingThreePidTask

    @Binds
    abstract fun bindDeleteThreePidTask(task: DefaultDeleteThreePidTask): DeleteThreePidTask
}
