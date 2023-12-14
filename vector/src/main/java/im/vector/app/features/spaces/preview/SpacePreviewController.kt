

package im.vector.app.features.spaces.preview

import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import im.vector.app.R
import im.vector.app.core.epoxy.loadingItem
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.ui.list.genericHeaderItem
import im.vector.app.core.utils.TextUtils
import im.vector.app.features.home.AvatarRenderer
import javax.inject.Inject

class SpacePreviewController @Inject constructor(
        private val avatarRenderer: AvatarRenderer,
        private val stringProvider: StringProvider
) : TypedEpoxyController<SpacePreviewState>() {

    interface InteractionListener

    var interactionListener: InteractionListener? = null

    override fun buildModels(data: SpacePreviewState?) {
        val host = this
        val memberCount = data?.spaceInfo?.invoke()?.memberCount ?: 0

        spaceTopSummaryItem {
            id("info")
            formattedMemberCount(host.stringProvider.getQuantityString(R.plurals.room_title_members, memberCount, memberCount))
            topic(data?.spaceInfo?.invoke()?.topic ?: data?.topic ?: "")
        }

        val result = data?.childInfoList?.invoke() ?: return
        if (result.isNotEmpty()) {
            genericHeaderItem {
                id("header_rooms")
                text(host.stringProvider.getString(R.string.rooms))
            }

            buildChildren(result, 0)
        }
    }

    private fun buildChildren(children: List<ChildInfo>, depth: Int) {
        val host = this
        children.forEach { child ->

            if (child.isSubSpace == true) {
                subSpaceItem {
                    id(child.roomId)
                    roomId(child.roomId)
                    title(child.name)
                    depth(depth)
                    avatarUrl(child.avatarUrl)
                    avatarRenderer(host.avatarRenderer)
                }
                when (child.children) {
                    is Loading -> {
                        loadingItem { id("loading_children_${child.roomId}") }
                    }
                    is Success -> {
                        buildChildren(child.children.invoke(), depth + 1)
                    }
                    else       -> {
                    }
                }
            } else {
                roomChildItem {
                    id(child.roomId)
                    depth(depth)
                    roomId(child.roomId)
                    title(child.name ?: "")
                    topic(child.topic ?: "")
                    avatarUrl(child.avatarUrl)
                    memberCount(TextUtils.formatCountToShortDecimal(child.memberCount ?: 0))
                    avatarRenderer(host.avatarRenderer)
                }
            }
        }
    }
}
