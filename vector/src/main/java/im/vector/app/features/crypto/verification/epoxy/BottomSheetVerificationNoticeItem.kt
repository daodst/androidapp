
package im.vector.app.features.crypto.verification.epoxy

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.lib.core.utils.epoxy.charsequence.EpoxyCharSequence


@EpoxyModelClass(layout = R2.layout.item_verification_notice)
abstract class BottomSheetVerificationNoticeItem : VectorEpoxyModel<BottomSheetVerificationNoticeItem.Holder>() {

    @EpoxyAttribute
    lateinit var notice: EpoxyCharSequence

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.notice.text = notice.charSequence
    }

    class Holder : VectorEpoxyHolder() {
        val notice by bind<TextView>(R.id.itemVerificationNoticeText)
    }
}
