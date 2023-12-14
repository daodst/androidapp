

package im.vector.app.features.spaces

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_space_beta_header)
abstract class SpaceBetaHeaderItem : VectorEpoxyModel<SpaceBetaHeaderItem.Holder>() {
    class Holder : VectorEpoxyHolder()
}
