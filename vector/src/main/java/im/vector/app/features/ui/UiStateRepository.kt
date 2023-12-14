

package im.vector.app.features.ui

import im.vector.app.features.home.RoomListDisplayMode


interface UiStateRepository {

    
    fun reset()

    fun getDisplayMode(): RoomListDisplayMode

    fun storeDisplayMode(displayMode: RoomListDisplayMode)

    
    fun storeSelectedSpace(spaceId: String?, sessionId: String)
    fun storeSelectedGroup(groupId: String?, sessionId: String)

    fun storeGroupingMethod(isSpace: Boolean, sessionId: String)

    fun getSelectedSpace(sessionId: String): String?
    fun getSelectedGroup(sessionId: String): String?
    fun isGroupingMethodSpace(sessionId: String): Boolean

    fun setCustomRoomDirectoryHomeservers(sessionId: String, servers: Set<String>)
    fun getCustomRoomDirectoryHomeservers(sessionId: String): Set<String>
}
