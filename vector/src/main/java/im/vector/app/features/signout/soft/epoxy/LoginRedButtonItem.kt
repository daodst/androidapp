

package im.vector.app.features.signout.soft.epoxy

import android.widget.Button
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.extensions.setTextOrHide

@EpoxyModelClass(layout = R2.layout.item_login_red_button)
abstract class LoginRedButtonItem : VectorEpoxyModel<LoginRedButtonItem.Holder>() {

    @EpoxyAttribute var text: String? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var listener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.button.setTextOrHide(text)
        holder.button.onClick(listener)
    }

    class Holder : VectorEpoxyHolder() {
        val button by bind<Button>(R.id.itemLoginRedButton)
    }
}
