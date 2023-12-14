
package im.vector.app.core.ui.list

import android.view.View
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel


@EpoxyModelClass(layout = R2.layout.item_vertical_margin)
abstract class VerticalMarginItem : VectorEpoxyModel<VerticalMarginItem.Holder>() {

    @EpoxyAttribute
    var heightInPx: Int = 0

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.space.updateLayoutParams {
            height = heightInPx
        }
    }

    class Holder : VectorEpoxyHolder() {
        val space by bind<View>(R.id.item_vertical_margin_space)
    }
}
