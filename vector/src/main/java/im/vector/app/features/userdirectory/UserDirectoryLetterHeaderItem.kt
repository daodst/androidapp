

package im.vector.app.features.userdirectory

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_user_directory_letter_header)
abstract class UserDirectoryLetterHeaderItem : VectorEpoxyModel<UserDirectoryLetterHeaderItem.Holder>() {

    @EpoxyAttribute var letter: String = ""

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.letterView.text = letter
    }

    class Holder : VectorEpoxyHolder() {
        val letterView by bind<TextView>(R.id.userDirectoryLetterView)
    }
}
