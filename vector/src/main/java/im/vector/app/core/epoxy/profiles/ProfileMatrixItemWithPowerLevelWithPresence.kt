

package im.vector.app.core.epoxy.profiles

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R2
import org.matrix.android.sdk.api.session.presence.model.UserPresence

@EpoxyModelClass(layout = R2.layout.item_profile_matrix_item)
abstract class ProfileMatrixItemWithPowerLevelWithPresence : ProfileMatrixItemWithPowerLevel() {

    @EpoxyAttribute var showPresence: Boolean = true
    @EpoxyAttribute var userPresence: UserPresence? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.presenceImageView.render(showPresence, userPresence)
    }
}
