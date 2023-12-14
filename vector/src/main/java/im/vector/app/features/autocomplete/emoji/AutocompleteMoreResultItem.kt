

package im.vector.app.features.autocomplete.emoji

import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_autocomplete_more_result)
abstract class AutocompleteMoreResultItem : VectorEpoxyModel<AutocompleteMoreResultItem.Holder>() {

    class Holder : VectorEpoxyHolder()
}
