

package im.vector.app.features.signout.soft.epoxy

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_login_header)
abstract class LoginHeaderItem : VectorEpoxyModel<LoginHeaderItem.Holder>() {
    class Holder : VectorEpoxyHolder()
}
