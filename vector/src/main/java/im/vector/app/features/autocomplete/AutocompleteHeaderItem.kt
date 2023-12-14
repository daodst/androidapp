

package im.vector.app.features.autocomplete

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_autocomplete_header_item)
abstract class AutocompleteHeaderItem : VectorEpoxyModel<AutocompleteHeaderItem.Holder>() {

    @EpoxyAttribute var title: String? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.titleView.text = title
    }

    class Holder : VectorEpoxyHolder() {
        val titleView by bind<TextView>(R.id.headerItemAutocompleteTitle)
    }
}
