

package org.matrix.android.sdk.internal.session.user.accountdata

import androidx.lifecycle.LiveData
import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.session.accountdata.SessionAccountDataService
import org.matrix.android.sdk.api.session.accountdata.UserAccountDataEvent
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.accountdata.RoomAccountDataEvent
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.api.util.awaitCallback
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.session.room.accountdata.RoomAccountDataDataSource
import org.matrix.android.sdk.internal.session.sync.handler.UserAccountDataSyncHandler
import org.matrix.android.sdk.internal.task.TaskExecutor
import org.matrix.android.sdk.internal.task.configureWith
import javax.inject.Inject

internal class DefaultSessionAccountDataService @Inject constructor(
        @SessionDatabase private val monarchy: Monarchy,
        private val updateUserAccountDataTask: UpdateUserAccountDataTask,
        private val userAccountDataSyncHandler: UserAccountDataSyncHandler,
        private val userAccountDataDataSource: UserAccountDataDataSource,
        private val roomAccountDataDataSource: RoomAccountDataDataSource,
        private val taskExecutor: TaskExecutor
) : SessionAccountDataService {

    override fun getUserAccountDataEvent(type: String): UserAccountDataEvent? {
        return userAccountDataDataSource.getAccountDataEvent(type)
    }

    override fun getLiveUserAccountDataEvent(type: String): LiveData<Optional<UserAccountDataEvent>> {
        return userAccountDataDataSource.getLiveAccountDataEvent(type)
    }

    override fun getUserAccountDataEvents(types: Set<String>): List<UserAccountDataEvent> {
        return userAccountDataDataSource.getAccountDataEvents(types)
    }

    override fun getLiveUserAccountDataEvents(types: Set<String>): LiveData<List<UserAccountDataEvent>> {
        return userAccountDataDataSource.getLiveAccountDataEvents(types)
    }

    override fun getRoomAccountDataEvents(types: Set<String>): List<RoomAccountDataEvent> {
        return roomAccountDataDataSource.getAccountDataEvents(null, types)
    }

    override fun getLiveRoomAccountDataEvents(types: Set<String>): LiveData<List<RoomAccountDataEvent>> {
        return roomAccountDataDataSource.getLiveAccountDataEvents(null, types)
    }

    override suspend fun updateUserAccountData(type: String, content: Content) {
        val params = UpdateUserAccountDataTask.AnyParams(type = type, any = content)
        awaitCallback<Unit> { callback ->
            updateUserAccountDataTask.configureWith(params) {
                this.retryCount = 5 
                this.callback = callback
            }
                    .executeBy(taskExecutor)
        }
        
        monarchy.runTransactionSync { realm ->
            userAccountDataSyncHandler.handleGenericAccountData(realm, type, content)
        }
    }
}