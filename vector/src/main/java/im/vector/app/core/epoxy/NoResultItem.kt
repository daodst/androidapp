

package im.vector.app.core.epoxy

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2

@EpoxyModelClass(layout = R2.layout.item_no_result)
abstract class NoResultItem : VectorEpoxyModel<NoResultItem.Holder>() {

    @EpoxyAttribute
    var text: String? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.textView.text = text
    }

    class Holder : VectorEpoxyHolder() {
        val textView by bind<TextView>(R.id.itemNoResultText)
    }
}
