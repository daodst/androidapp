

package im.vector.app.features.settings.homeserver

import im.vector.app.core.platform.VectorViewModelAction

sealed class HomeserverSettingsAction : VectorViewModelAction {
    object Refresh : HomeserverSettingsAction()
}
