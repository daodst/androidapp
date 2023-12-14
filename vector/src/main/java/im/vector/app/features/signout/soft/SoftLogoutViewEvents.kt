

package im.vector.app.features.signout.soft

import im.vector.app.core.platform.VectorViewEvents


sealed class SoftLogoutViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : SoftLogoutViewEvents()

    data class ErrorNotSameUser(val currentUserId: String, val newUserId: String) : SoftLogoutViewEvents()
    object ClearData : SoftLogoutViewEvents()
}
