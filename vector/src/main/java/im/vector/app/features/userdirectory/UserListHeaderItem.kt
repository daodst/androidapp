

package im.vector.app.features.userdirectory

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_user_list_header)
abstract class UserListHeaderItem : VectorEpoxyModel<UserListHeaderItem.Holder>() {

    @EpoxyAttribute var header: String = ""

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.headerTextView.text = header
    }

    class Holder : VectorEpoxyHolder() {
        val headerTextView by bind<TextView>(R.id.userListHeaderView)
    }
}
