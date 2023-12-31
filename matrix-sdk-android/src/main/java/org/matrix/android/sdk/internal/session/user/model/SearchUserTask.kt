

package org.matrix.android.sdk.internal.session.user.model

import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.user.SearchUserAPI
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface SearchUserTask : Task<SearchUserTask.Params, List<User>> {

    data class Params(
            val limit: Int,
            val search: String,
            val excludedUserIds: Set<String>
    )
}

internal class DefaultSearchUserTask @Inject constructor(
        private val searchUserAPI: SearchUserAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) : SearchUserTask {

    override suspend fun execute(params: SearchUserTask.Params): List<User> {
        val response = executeRequest(globalErrorReceiver) {
            searchUserAPI.searchUsers(SearchUsersParams(params.search, params.limit))
        }
        return response.users.map {
            User(it.userId, it.displayName, it.avatarUrl, it.tel_numbers)
        }
    }
}
