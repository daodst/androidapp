
package im.vector.app.features.crypto.verification.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.ui.views.QrCodeImageView


@EpoxyModelClass(layout = R2.layout.item_verification_qr_code)
abstract class BottomSheetVerificationQrCodeItem : VectorEpoxyModel<BottomSheetVerificationQrCodeItem.Holder>() {

    @EpoxyAttribute
    lateinit var data: String

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.qsrCodeImage.setData(data)
    }

    class Holder : VectorEpoxyHolder() {
        val qsrCodeImage by bind<QrCodeImageView>(R.id.itemVerificationQrCodeImage)
    }
}
