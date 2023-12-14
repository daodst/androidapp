
package im.vector.app.core.epoxy

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2

@EpoxyModelClass(layout = R2.layout.item_divider)
abstract class DividerItem : VectorEpoxyModel<DividerItem.Holder>() {
    class Holder : VectorEpoxyHolder()
}
