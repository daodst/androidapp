

package im.vector.app.features.roomdirectory.roompreview

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.features.roomdirectory.JoinState
import org.matrix.android.sdk.api.session.permalinks.PermalinkData
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.util.MatrixItem

data class RoomPreviewViewState(
        val peekingState: Async<PeekingState> = Uninitialized,
        
        val roomId: String = "",
        val roomAlias: String? = null,
        val roomType: String? = null,

        val roomName: String? = null,
        val roomTopic: String? = null,
        val numJoinMembers: Int? = null,
        val avatarUrl: String? = null,

        val shouldPeekFromServer: Boolean = false,
        
        val homeServers: List<String> = emptyList(),
        
        val roomJoinState: JoinState = JoinState.NOT_JOINED,
        
        val lastError: Throwable? = null,

        val fromEmailInvite: PermalinkData.RoomEmailInviteLink? = null,
        
        val isEmailBoundToAccount: Boolean = false
) : MavericksState {

    constructor(args: RoomPreviewData) : this(
            roomId = args.roomId,
            roomAlias = args.roomAlias,
            homeServers = args.homeServers,
            roomName = args.roomName,
            roomTopic = args.topic,
            numJoinMembers = args.numJoinedMembers,
            avatarUrl = args.avatarUrl,
            shouldPeekFromServer = args.peekFromServer,
            fromEmailInvite = args.fromEmailInvite,
            roomType = args.roomType
    )

    fun matrixItem(): MatrixItem {
        return if (roomType == RoomType.SPACE) MatrixItem.SpaceItem(roomId, roomName ?: roomAlias, avatarUrl)
        else MatrixItem.RoomItem(roomId, roomName ?: roomAlias, avatarUrl)
    }
}
