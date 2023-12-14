

package im.vector.app.features.onboarding.ftueauth

import android.widget.ImageView
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel

@EpoxyModelClass(layout = R2.layout.item_splash_carousel)
abstract class SplashCarouselItem : VectorEpoxyModel<SplashCarouselItem.Holder>() {

    @EpoxyAttribute
    lateinit var item: SplashCarouselState.Item

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.view.setBackgroundResource(item.pageBackground)
        holder.image.setImageResource(item.image)
        holder.title.text = item.title.charSequence
        holder.body.setText(item.body)
    }

    class Holder : VectorEpoxyHolder() {
        val image by bind<ImageView>(R.id.carousel_item_image)
        val title by bind<TextView>(R.id.carousel_item_title)
        val body by bind<TextView>(R.id.carousel_item_body)
    }
}
