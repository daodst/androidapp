

package im.vector.lib.attachmentviewer

import android.view.View
import im.vector.lib.attachmentviewer.databinding.ItemImageAttachmentBinding

class ZoomableImageViewHolder constructor(itemView: View) :
        BaseViewHolder(itemView) {

    val views = ItemImageAttachmentBinding.bind(itemView)

    init {
        views.touchImageView.setAllowParentInterceptOnEdge(false)
        views.touchImageView.setOnScaleChangeListener { scaleFactor, _, _ ->
            
            
            
            views.touchImageView.setAllowParentInterceptOnEdge(scaleFactor <= 1.0008f)
        }
        views.touchImageView.setScale(1.0f, true)
        views.touchImageView.setAllowParentInterceptOnEdge(true)
    }

    internal val target = DefaultImageLoaderTarget.ZoomableImageTarget(this, views.touchImageView)

    override fun onRecycled() {
        super.onRecycled()
        views.touchImageView.setImageDrawable(null)
    }
}
