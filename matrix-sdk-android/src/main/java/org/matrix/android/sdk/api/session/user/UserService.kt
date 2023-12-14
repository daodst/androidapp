

package org.matrix.android.sdk.api.session.user

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.util.Optional


interface UserService {

    
    fun getUser(userId: String): User?

    suspend fun resolveUserOnline(userId: String): User

    
    suspend fun resolveUser(userId: String): User

    
    suspend fun searchUsersDirectory(search: String, limit: Int, excludedUserIds: Set<String>): List<User>

    
    fun getUserLive(userId: String): LiveData<Optional<User>>

    
    fun getUsersLive(): LiveData<List<User>>

    
    fun getPagedUsersLive(filter: String? = null, excludedUserIds: Set<String>? = null): LiveData<PagedList<User>>

    
    fun getIgnoredUsersLive(): LiveData<List<User>>

    
    suspend fun ignoreUserIds(userIds: List<String>)

    
    suspend fun unIgnoreUserIds(userIds: List<String>)

    suspend fun getServerPubKeyTask():String
}
