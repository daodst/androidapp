

package im.vector.app.features.home.room.list

import im.vector.app.features.home.RoomListDisplayMode

interface RoomListSectionBuilder {
    fun buildSections(mode: RoomListDisplayMode): List<RoomsSection>
}
