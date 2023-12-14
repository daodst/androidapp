

package im.vector.app.features.roomprofile.settings.joinrule

import im.vector.app.core.ui.bottomsheet.BottomSheetGenericRadioAction
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules

class RoomJoinRuleRadioAction(
        val roomJoinRule: RoomJoinRules,
        title: String,
        description: String,
        isSelected: Boolean
) : BottomSheetGenericRadioAction(
        title = title,
        isSelected = isSelected,
        description = description
)
