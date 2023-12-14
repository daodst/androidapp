

package im.vector.app.features.home.room.detail.timeline.factory

import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.utils.DimensionConverter
import im.vector.app.features.home.room.detail.timeline.helper.AvatarSizeProvider
import im.vector.app.features.home.room.detail.timeline.helper.TimelineMediaSizeProvider
import im.vector.app.features.home.room.detail.timeline.item.AbsMessageItem
import im.vector.app.features.home.room.detail.timeline.item.MessageLiveLocationStartItem
import im.vector.app.features.home.room.detail.timeline.item.MessageLiveLocationStartItem_
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.room.model.livelocation.LiveLocationBeaconContent
import javax.inject.Inject

class LiveLocationMessageItemFactory @Inject constructor(
        private val dimensionConverter: DimensionConverter,
        private val timelineMediaSizeProvider: TimelineMediaSizeProvider,
        private val avatarSizeProvider: AvatarSizeProvider,
) {

    fun create(
            liveLocationContent: LiveLocationBeaconContent,
            highlight: Boolean,
            attributes: AbsMessageItem.Attributes,
    ): VectorEpoxyModel<*>? {
        
        return when {
            isLiveRunning(liveLocationContent) -> buildStartLiveItem(highlight, attributes)
            else                               -> null
        }
    }

    private fun isLiveRunning(liveLocationContent: LiveLocationBeaconContent): Boolean {
        return liveLocationContent.getBestBeaconInfo()?.isLive.orFalse() && liveLocationContent.hasTimedOut.not()
    }

    private fun buildStartLiveItem(
            highlight: Boolean,
            attributes: AbsMessageItem.Attributes,
    ): MessageLiveLocationStartItem {
        val width = timelineMediaSizeProvider.getMaxSize().first
        val height = dimensionConverter.dpToPx(MessageItemFactory.MESSAGE_LOCATION_ITEM_HEIGHT_IN_DP)

        return MessageLiveLocationStartItem_()
                .attributes(attributes)
                .mapWidth(width)
                .mapHeight(height)
                .highlighted(highlight)
                .leftGuideline(avatarSizeProvider.leftGuideline)
    }
}
