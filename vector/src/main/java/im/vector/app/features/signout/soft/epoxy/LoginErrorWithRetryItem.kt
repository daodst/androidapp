

package im.vector.app.features.signout.soft.epoxy

import android.widget.Button
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick

@EpoxyModelClass(layout = R2.layout.item_login_error_retry)
abstract class LoginErrorWithRetryItem : VectorEpoxyModel<LoginErrorWithRetryItem.Holder>() {

    @EpoxyAttribute
    var text: String? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var listener: ClickListener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.textView.text = text
        holder.buttonView.onClick(listener)
    }

    class Holder : VectorEpoxyHolder() {
        val textView by bind<TextView>(R.id.itemLoginErrorRetryText)
        val buttonView by bind<Button>(R.id.itemLoginErrorRetryButton)
    }
}
