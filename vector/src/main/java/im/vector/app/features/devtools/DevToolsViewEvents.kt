

package im.vector.app.features.devtools

import im.vector.app.core.platform.VectorViewEvents

sealed class DevToolsViewEvents : VectorViewEvents {
    object Dismiss : DevToolsViewEvents()

    
    data class ShowAlertMessage(val message: String) : DevToolsViewEvents()
    data class ShowSnackMessage(val message: String) : DevToolsViewEvents()
}
