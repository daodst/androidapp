
package im.vector.app.core.epoxy

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2

@EpoxyModelClass(layout = R2.layout.item_divider_on_surface)
abstract class BottomSheetDividerItem : VectorEpoxyModel<BottomSheetDividerItem.Holder>() {
    class Holder : VectorEpoxyHolder()
}
