

package im.vector.app.core.ui.bottomsheet

import im.vector.app.core.epoxy.bottomsheet.BottomSheetRadioActionItem_
import im.vector.app.core.platform.VectorSharedAction


open class BottomSheetGenericRadioAction(
        open val title: String?,
        open val description: String? = null,
        open val isSelected: Boolean
) : VectorSharedAction {

    fun toRadioBottomSheetItem(): BottomSheetRadioActionItem_ {
        return BottomSheetRadioActionItem_().also {
            it.id("action_$title")
            it.title(title)
            it.selected(isSelected)
            it.description(description)
        }
    }
}
