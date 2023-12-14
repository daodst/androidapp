
package im.vector.app.features.home.room.detail.timeline.item

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_timeline_event_blank_stub)
abstract class BlankItem : VectorEpoxyModel<BlankItem.BlankHolder>() {
    class BlankHolder : VectorEpoxyHolder()
}
