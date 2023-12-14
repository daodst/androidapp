

package im.vector.app.features.spaces.invite

import im.vector.app.core.platform.VectorViewEvents

sealed class SpaceInviteBottomSheetEvents : VectorViewEvents {
    data class ShowError(val message: String) : SpaceInviteBottomSheetEvents()
}
