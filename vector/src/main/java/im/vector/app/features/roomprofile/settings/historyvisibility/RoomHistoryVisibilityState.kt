

package im.vector.app.features.roomprofile.settings.historyvisibility

import im.vector.app.core.ui.bottomsheet.BottomSheetGenericState
import org.matrix.android.sdk.api.session.room.model.RoomHistoryVisibility

data class RoomHistoryVisibilityState(
        val currentRoomHistoryVisibility: RoomHistoryVisibility = RoomHistoryVisibility.SHARED
) : BottomSheetGenericState() {

    constructor(args: RoomHistoryVisibilityBottomSheetArgs) : this(currentRoomHistoryVisibility = args.currentRoomHistoryVisibility)
}
