

package org.matrix.android.sdk.api.session.room.peeking

import org.matrix.android.sdk.api.util.MatrixItem

sealed class PeekResult {
    data class Success(
            val roomId: String,
            val alias: String?,
            val name: String?,
            val topic: String?,
            val avatarUrl: String?,
            val numJoinedMembers: Int?,
            val roomType: String?,
            val viaServers: List<String>,
            val someMembers: List<MatrixItem.UserItem>?,
            val isPublic: Boolean
    ) : PeekResult()

    data class PeekingNotAllowed(
            val roomId: String,
            val alias: String?,
            val viaServers: List<String>
    ) : PeekResult()

    object UnknownAlias : PeekResult()

    fun isSuccess() = this is Success
}
