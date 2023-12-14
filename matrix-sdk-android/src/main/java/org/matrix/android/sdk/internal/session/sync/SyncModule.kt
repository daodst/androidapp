

package org.matrix.android.sdk.internal.session.sync

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.matrix.android.sdk.internal.session.SessionScope
import retrofit2.Retrofit

@Module
internal abstract class SyncModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @SessionScope
        fun providesSyncAPI(retrofit: Retrofit): SyncAPI {
            return retrofit.create(SyncAPI::class.java)
        }
    }

    @Binds
    abstract fun bindSyncTask(task: DefaultSyncTask): SyncTask

    @Binds
    abstract fun bindRoomSyncEphemeralTemporaryStore(store: RoomSyncEphemeralTemporaryStoreFile): RoomSyncEphemeralTemporaryStore
}
