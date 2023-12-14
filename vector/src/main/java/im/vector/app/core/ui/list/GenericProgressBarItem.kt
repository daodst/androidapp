
package im.vector.app.core.ui.list

import android.widget.ProgressBar
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel


@EpoxyModelClass(layout = R2.layout.item_generic_progress)
abstract class GenericProgressBarItem : VectorEpoxyModel<GenericProgressBarItem.Holder>() {

    @EpoxyAttribute
    var progress: Int = 0

    @EpoxyAttribute
    var total: Int = 100

    @EpoxyAttribute
    var indeterminate: Boolean = false

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.progressbar.progress = progress
        holder.progressbar.max = total
        holder.progressbar.isIndeterminate = indeterminate
    }

    class Holder : VectorEpoxyHolder() {
        val progressbar by bind<ProgressBar>(R.id.genericProgressBar)
    }
}
