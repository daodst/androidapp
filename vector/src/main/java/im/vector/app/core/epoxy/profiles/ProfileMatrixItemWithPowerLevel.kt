

package im.vector.app.core.epoxy.profiles

import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import im.vector.app.core.extensions.setTextOrHide

@EpoxyModelClass(layout = R2.layout.item_profile_matrix_item)
abstract class ProfileMatrixItemWithPowerLevel : ProfileMatrixItem() {

    @EpoxyAttribute var ignoredUser: Boolean = false
    @EpoxyAttribute var powerLevelLabel: CharSequence? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.editableView.isVisible = false
        holder.ignoredUserView.isVisible = ignoredUser
        holder.powerLabel.setTextOrHide(powerLevelLabel)
    }
}
