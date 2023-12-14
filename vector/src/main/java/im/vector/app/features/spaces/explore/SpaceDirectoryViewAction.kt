

package im.vector.app.features.spaces.explore

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo

sealed class SpaceDirectoryViewAction : VectorViewModelAction {
    data class ExploreSubSpace(val spaceChildInfo: SpaceChildInfo) : SpaceDirectoryViewAction()
    data class JoinOrOpen(val spaceChildInfo: SpaceChildInfo) : SpaceDirectoryViewAction()
    data class ShowDetails(val spaceChildInfo: SpaceChildInfo) : SpaceDirectoryViewAction()
    data class NavigateToRoom(val roomId: String) : SpaceDirectoryViewAction()
    object CreateNewRoom : SpaceDirectoryViewAction()
    object HandleBack : SpaceDirectoryViewAction()
    object Retry : SpaceDirectoryViewAction()
    data class RefreshUntilFound(val roomIdToFind: String) : SpaceDirectoryViewAction()
    object LoadAdditionalItemsIfNeeded : SpaceDirectoryViewAction()
}
