

package im.vector.app.features.home

import im.vector.app.core.platform.VectorViewEvents

sealed class HomeDetailViewEvents : VectorViewEvents {
    object Loading : HomeDetailViewEvents()
    object CallStarted : HomeDetailViewEvents()
    data class FailToCall(val failure: Throwable) : HomeDetailViewEvents()
}
