
package im.vector.app.features.crypto.verification.epoxy

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel


@EpoxyModelClass(layout = R2.layout.item_verification_decimal_code)
abstract class BottomSheetVerificationDecimalCodeItem : VectorEpoxyModel<BottomSheetVerificationDecimalCodeItem.Holder>() {

    @EpoxyAttribute
    var code: String = ""

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.code.text = code
    }

    class Holder : VectorEpoxyHolder() {
        val code by bind<TextView>(R.id.itemVerificationDecimalCode)
    }
}
