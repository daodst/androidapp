
package im.vector.app.core.ui.bottomsheet

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.extensions.setTextOrHide


@EpoxyModelClass(layout = R2.layout.item_bottom_sheet_title)
abstract class BottomSheetTitleItem : VectorEpoxyModel<BottomSheetTitleItem.Holder>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    var subTitle: String? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.title.text = title
        holder.subtitle.setTextOrHide(subTitle)
    }

    class Holder : VectorEpoxyHolder() {
        val title by bind<TextView>(R.id.itemBottomSheetTitleTitle)
        val subtitle by bind<TextView>(R.id.itemBottomSheetTitleSubtitle)
    }
}
