

package im.vector.app.core.ui.list

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel


@EpoxyModelClass(layout = R2.layout.item_generic_loader)
abstract class GenericLoaderItem : VectorEpoxyModel<GenericLoaderItem.Holder>() {

    

    class Holder : VectorEpoxyHolder()
}
