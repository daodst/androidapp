

package im.vector.app.core.epoxy

import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.extensions.setTextOrHide

@EpoxyModelClass(layout = R2.layout.item_loading)
abstract class LoadingItem : VectorEpoxyModel<LoadingItem.Holder>() {

    @EpoxyAttribute var loadingText: String? = null
    @EpoxyAttribute var showLoader: Boolean = true

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.progressBar.isVisible = showLoader
        holder.textView.setTextOrHide(loadingText)
    }

    class Holder : VectorEpoxyHolder() {
        val textView by bind<TextView>(R.id.loadingText)
        val progressBar by bind<ProgressBar>(R.id.loadingProgress)
    }
}
