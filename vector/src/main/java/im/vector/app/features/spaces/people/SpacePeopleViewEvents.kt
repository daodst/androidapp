

package im.vector.app.features.spaces.people

import im.vector.app.core.platform.VectorViewEvents

sealed class SpacePeopleViewEvents : VectorViewEvents {
    data class OpenRoom(val roomId: String) : SpacePeopleViewEvents()
    data class InviteToSpace(val spaceId: String) : SpacePeopleViewEvents()
}
