

package im.vector.app.features.signout.soft.epoxy

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.extensions.setTextOrHide

@EpoxyModelClass(layout = R2.layout.item_login_title)
abstract class LoginTitleItem : VectorEpoxyModel<LoginTitleItem.Holder>() {

    @EpoxyAttribute var text: String? = null

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.textView.setTextOrHide(text)
    }

    class Holder : VectorEpoxyHolder() {
        val textView by bind<TextView>(R.id.itemLoginTitleText)
    }
}
