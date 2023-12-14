

package im.vector.app.features.home

import androidx.annotation.StringRes
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.R
import im.vector.app.RoomGroupingMethod
import org.matrix.android.sdk.api.session.initsync.SyncStatusService
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.sync.SyncState
import org.matrix.android.sdk.api.util.MatrixItem

data class HomeDetailViewState(
        val roomGroupingMethod: RoomGroupingMethod = RoomGroupingMethod.BySpace(null),
        val myMatrixItem: MatrixItem? = null,
        val asyncRooms: Async<List<RoomSummary>> = Uninitialized,
        
        val currentTab: HomeTab = HomeTab.RoomList(RoomListDisplayMode.PEOPLE),
        val notificationCountCatchup: Int = 0,
        val notificationHighlightCatchup: Boolean = false,
        val notificationCountPeople: Int = 0,
        val notificationHighlightPeople: Boolean = false,

        
        val notificationNormalCountPeople: Int = 0,
        val notificationNormalHighlightPeople: Boolean = false,
        
        val notificationGroupCountPeople: Int = 0,
        val notificationGroupHighlightPeople: Boolean = false,
        
        val notificationCountRooms: Int = 0,
        val notificationHighlightRooms: Boolean = false,
        val hasUnreadMessages: Boolean = false,
        val syncState: SyncState = SyncState.Idle,
        val incrementalSyncStatus: SyncStatusService.Status.IncrementalSyncStatus = SyncStatusService.Status.IncrementalSyncIdle,
        val pushCounter: Int = 0,
        val pstnSupportFlag: Boolean = false,
        val forceDialPadTab: Boolean = false
) : MavericksState {
    val showDialPadTab = forceDialPadTab || pstnSupportFlag
}

sealed class HomeTab(@StringRes val titleRes: Int) {
    data class RoomList(val displayMode: RoomListDisplayMode) : HomeTab(displayMode.titleRes)
    object DialPad : HomeTab(R.string.call_dial_pad_title)
}
