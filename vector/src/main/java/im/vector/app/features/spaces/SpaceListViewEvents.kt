

package im.vector.app.features.spaces

import im.vector.app.core.platform.VectorViewEvents


sealed class SpaceListViewEvents : VectorViewEvents {
    data class OpenSpace(val groupingMethodHasChanged: Boolean) : SpaceListViewEvents()
    data class OpenSpaceSummary(val id: String) : SpaceListViewEvents()
    data class OpenSpaceInvite(val id: String) : SpaceListViewEvents()
    object AddSpace : SpaceListViewEvents()
    data class OpenGroup(val groupingMethodHasChanged: Boolean) : SpaceListViewEvents()
}
