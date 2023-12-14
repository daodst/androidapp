

package im.vector.app.features.home.room.list

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_room_placeholder)
abstract class RoomSummaryItemPlaceHolder : VectorEpoxyModel<RoomSummaryItemPlaceHolder.Holder>() {
    class Holder : VectorEpoxyHolder()
}
