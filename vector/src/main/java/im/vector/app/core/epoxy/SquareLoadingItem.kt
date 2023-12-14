

package im.vector.app.core.epoxy

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2

@EpoxyModelClass(layout = R2.layout.item_loading_square)
abstract class SquareLoadingItem : VectorEpoxyModel<SquareLoadingItem.Holder>() {

    class Holder : VectorEpoxyHolder()
}
