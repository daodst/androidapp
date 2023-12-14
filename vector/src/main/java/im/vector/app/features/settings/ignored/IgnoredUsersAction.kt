

package im.vector.app.features.settings.ignored

import im.vector.app.core.platform.VectorViewModelAction

sealed class IgnoredUsersAction : VectorViewModelAction {
    data class UnIgnore(val userId: String) : IgnoredUsersAction()
}
