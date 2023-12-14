

package im.vector.app.features.room

import im.vector.app.core.platform.VectorViewEvents

sealed class RequireActiveMembershipViewEvents : VectorViewEvents {
    data class RoomLeft(val leftMessage: String?) : RequireActiveMembershipViewEvents()
}
