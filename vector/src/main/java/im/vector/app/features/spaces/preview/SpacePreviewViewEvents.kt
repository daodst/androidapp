

package im.vector.app.features.spaces.preview

import im.vector.app.core.platform.VectorViewEvents

sealed class SpacePreviewViewEvents : VectorViewEvents {
    object Dismiss : SpacePreviewViewEvents()
    object JoinSuccess : SpacePreviewViewEvents()
    data class JoinFailure(val message: String?) : SpacePreviewViewEvents()
}
