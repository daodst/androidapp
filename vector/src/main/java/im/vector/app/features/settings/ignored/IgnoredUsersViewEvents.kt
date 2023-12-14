

package im.vector.app.features.settings.ignored

import im.vector.app.core.platform.VectorViewEvents


sealed class IgnoredUsersViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : IgnoredUsersViewEvents()
    data class Failure(val throwable: Throwable) : IgnoredUsersViewEvents()
    object Success : IgnoredUsersViewEvents()
}
