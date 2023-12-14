

package im.vector.app.features.home.room.breadcrumbs

import com.airbnb.epoxy.EpoxyController
import im.vector.app.core.epoxy.zeroItem
import im.vector.app.features.home.AvatarRenderer
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

class BreadcrumbsController @Inject constructor(
        private val avatarRenderer: AvatarRenderer
) : EpoxyController() {

    var listener: Listener? = null

    private var viewState: BreadcrumbsViewState? = null

    fun update(viewState: BreadcrumbsViewState) {
        this.viewState = viewState
        requestModelBuild()
    }

    override fun buildModels() {
        val safeViewState = viewState ?: return
        val host = this
        
        zeroItem {
            id("top")
        }

        
        
        safeViewState.asyncBreadcrumbs.invoke()
                ?.forEach { roomSummary ->
                    breadcrumbsItem {
                        id(roomSummary.roomId)
                        hasTypingUsers(roomSummary.typingUsers.isNotEmpty())
                        avatarRenderer(host.avatarRenderer)
                        matrixItem(roomSummary.toMatrixItem())
                        unreadNotificationCount(roomSummary.notificationCount)
                        showHighlighted(roomSummary.highlightCount > 0)
                        hasUnreadMessage(roomSummary.hasUnreadMessages)
                        hasDraft(roomSummary.userDrafts.isNotEmpty())
                        itemClickListener {
                            host.listener?.onBreadcrumbClicked(roomSummary.roomId)
                        }
                    }
                }
    }

    interface Listener {
        fun onBreadcrumbClicked(roomId: String)
    }
}
