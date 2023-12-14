

package im.vector.app.features.home.room.detail.timeline.item

import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import im.vector.app.R
import im.vector.app.R2
import im.vector.app.core.glide.GlideApp
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.features.home.room.detail.timeline.style.TimelineMessageLayout
import im.vector.app.features.home.room.detail.timeline.style.granularRoundedCorners
import im.vector.app.features.themes.ThemeUtils

@EpoxyModelClass(layout = R2.layout.item_timeline_event_base)
abstract class MessageLiveLocationStartItem : AbsMessageItem<MessageLiveLocationStartItem.Holder>() {

    @EpoxyAttribute
    var mapWidth: Int = 0

    @EpoxyAttribute
    var mapHeight: Int = 0

    override fun bind(holder: Holder) {
        super.bind(holder)
        renderSendState(holder.view, null)
        bindMap(holder)
        bindBottomBanner(holder)
    }

    private fun bindMap(holder: Holder) {
        val messageLayout = attributes.informationData.messageLayout
        val mapCornerTransformation = if (messageLayout is TimelineMessageLayout.Bubble) {
            messageLayout.cornersRadius.granularRoundedCorners()
        } else {
            RoundedCorners(getDefaultLayoutCornerRadiusInDp(holder))
        }
        holder.noLocationMapImageView.updateLayoutParams {
            width = mapWidth
            height = mapHeight
        }
        GlideApp.with(holder.noLocationMapImageView)
                .load(R.drawable.bg_no_location_map)
                .transform(mapCornerTransformation)
                .into(holder.noLocationMapImageView)
    }

    private fun bindBottomBanner(holder: Holder) {
        val messageLayout = attributes.informationData.messageLayout
        val imageCornerTransformation = if (messageLayout is TimelineMessageLayout.Bubble) {
            GranularRoundedCorners(0f, 0f, messageLayout.cornersRadius.bottomEndRadius, messageLayout.cornersRadius.bottomStartRadius)
        } else {
            val bottomCornerRadius = getDefaultLayoutCornerRadiusInDp(holder).toFloat()
            GranularRoundedCorners(0f, 0f, bottomCornerRadius, bottomCornerRadius)
        }
        GlideApp.with(holder.bannerImageView)
                .load(ColorDrawable(ThemeUtils.getColor(holder.bannerImageView.context, R.attr.colorSurface)))
                .transform(imageCornerTransformation)
                .into(holder.bannerImageView)
    }

    private fun getDefaultLayoutCornerRadiusInDp(holder: Holder): Int {
        val dimensionConverter = DimensionConverter(holder.view.resources)
        return dimensionConverter.dpToPx(8)
    }

    override fun getViewStubId() = STUB_ID

    class Holder : AbsMessageItem.Holder(STUB_ID) {
        val bannerImageView by bind<ImageView>(R.id.locationLiveStartBanner)
        val noLocationMapImageView by bind<ImageView>(R.id.locationLiveStartMap)
    }

    companion object {
        private var STUB_ID = R.id.messageContentLiveLocationStartStub
    }
}
