

package im.vector.app.features.roomprofile.uploads.files

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.extensions.setTextOrHide

@EpoxyModelClass(layout = R2.layout.item_uploads_file)
abstract class UploadsFileItem : VectorEpoxyModel<UploadsFileItem.Holder>() {

    @EpoxyAttribute var title: String? = null
    @EpoxyAttribute var subtitle: String? = null

    @EpoxyAttribute var listener: Listener? = null

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.view.setOnClickListener { listener?.onItemClicked() }
        holder.titleView.text = title
        holder.subtitleView.setTextOrHide(subtitle)
        holder.downloadView.setOnClickListener { listener?.onDownloadClicked() }
        holder.shareView.setOnClickListener { listener?.onShareClicked() }
    }

    class Holder : VectorEpoxyHolder() {
        val titleView by bind<TextView>(R.id.uploadsFileTitle)
        val subtitleView by bind<TextView>(R.id.uploadsFileSubtitle)
        val downloadView by bind<View>(R.id.uploadsFileActionDownload)
        val shareView by bind<View>(R.id.uploadsFileActionShare)
    }

    interface Listener {
        fun onItemClicked()
        fun onDownloadClicked()
        fun onShareClicked()
    }
}
