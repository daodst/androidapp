
package im.vector.app.features.crypto.verification.epoxy

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel


@EpoxyModelClass(layout = R2.layout.item_verification_wait)
abstract class BottomSheetSelfWaitItem : VectorEpoxyModel<BottomSheetSelfWaitItem.Holder>() {
    class Holder : VectorEpoxyHolder()
}
