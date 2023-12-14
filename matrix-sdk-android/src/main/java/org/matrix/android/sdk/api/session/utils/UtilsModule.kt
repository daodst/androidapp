

package org.matrix.android.sdk.api.session.utils

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.api.session.utils.model.AutoJoinRoomTask
import org.matrix.android.sdk.api.session.utils.model.DefaultAutoJoinRoomTask
import org.matrix.android.sdk.api.session.utils.model.DefaultGetMyJoinedRoomsTask
import org.matrix.android.sdk.api.session.utils.model.DefaultInviteRoomPayStatusTask
import org.matrix.android.sdk.api.session.utils.model.DefaultMediaInfoTask
import org.matrix.android.sdk.api.session.utils.model.DefaultPledgeBeanTask
import org.matrix.android.sdk.api.session.utils.model.DefaultRecent7DayActiveTask
import org.matrix.android.sdk.api.session.utils.model.DefaultRoomOwnerTask
import org.matrix.android.sdk.api.session.utils.model.DefaultSearchUserByPhoneTask
import org.matrix.android.sdk.api.session.utils.model.DefaultTodayIsActiveTask
import org.matrix.android.sdk.api.session.utils.model.DefaultUserInfoTask
import org.matrix.android.sdk.api.session.utils.model.DefaultUserLevelTask
import org.matrix.android.sdk.api.session.utils.model.DefaultUserLimitByAddrTask
import org.matrix.android.sdk.api.session.utils.model.GetMyJoinedRoomsTask
import org.matrix.android.sdk.api.session.utils.model.InviteRoomPayStatusTask
import org.matrix.android.sdk.api.session.utils.model.MediaInfoTask
import org.matrix.android.sdk.api.session.utils.model.PledgeBeanTask
import org.matrix.android.sdk.api.session.utils.model.Recent7DayActiveTask
import org.matrix.android.sdk.api.session.utils.model.RoomOwnerTask
import org.matrix.android.sdk.api.session.utils.model.SearchUserByPhoneTask
import org.matrix.android.sdk.api.session.utils.model.TodayIsActiveTask
import org.matrix.android.sdk.api.session.utils.model.UserInfoTask
import org.matrix.android.sdk.api.session.utils.model.UserLevelTask
import org.matrix.android.sdk.api.session.utils.model.UserLimitByAddrTask
import org.matrix.android.sdk.internal.session.SessionScope
import retrofit2.Retrofit

@Module
internal abstract class UtilsModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        @SessionScope
        fun providesUtilsAPI(retrofit: Retrofit): UtilsAPI {
            return retrofit.create(UtilsAPI::class.java)
        }
    }

    @Binds
    abstract fun bindUtilsService(service: DefaultUtilsService): UtilsService

    @Binds
    abstract fun bindDefaultPledgeBeanTask(service: DefaultPledgeBeanTask): PledgeBeanTask

    @Binds
    abstract fun bindDefaultSearchUserByPhoneTask(service: DefaultSearchUserByPhoneTask): SearchUserByPhoneTask

    @Binds
    abstract fun bindInviteRoomPayStatusTask(service: DefaultInviteRoomPayStatusTask): InviteRoomPayStatusTask

    @Binds
    abstract fun bindRoomOwnerTask(service: DefaultRoomOwnerTask): RoomOwnerTask

    @Binds
    abstract fun bindUserLevelTask(service: DefaultUserLevelTask): UserLevelTask

    @Binds
    abstract fun bindUserInfoTask(service: DefaultUserInfoTask): UserInfoTask

    @Binds
    abstract fun bindMediaInfoTask(service: DefaultMediaInfoTask): MediaInfoTask

    @Binds
    abstract fun bindUserLimitByAddrTask(service: DefaultUserLimitByAddrTask): UserLimitByAddrTask

    @Binds
    abstract fun bindTodayIsActiveTask(service: DefaultTodayIsActiveTask): TodayIsActiveTask

    @Binds
    abstract fun bindRecent7DayActiveTask(service: DefaultRecent7DayActiveTask): Recent7DayActiveTask

    @Binds
    abstract fun bindGetMyJoinedRoomsTask(service: DefaultGetMyJoinedRoomsTask): GetMyJoinedRoomsTask

    @Binds
    abstract fun bindAutoJoinRoomTask(service: DefaultAutoJoinRoomTask): AutoJoinRoomTask
}
