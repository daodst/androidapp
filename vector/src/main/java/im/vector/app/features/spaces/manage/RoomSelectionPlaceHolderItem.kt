

package im.vector.app.features.spaces.manage

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_room_to_add_in_space_placeholder)
abstract class RoomSelectionPlaceHolderItem : VectorEpoxyModel<RoomSelectionPlaceHolderItem.Holder>() {
    class Holder : VectorEpoxyHolder()
}
