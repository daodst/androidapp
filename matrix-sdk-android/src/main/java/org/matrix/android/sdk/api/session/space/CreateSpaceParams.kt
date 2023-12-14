

package org.matrix.android.sdk.api.session.space

import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams

class CreateSpaceParams : CreateRoomParams() {

    init {
        
        roomType = RoomType.SPACE

        
        
        powerLevelContentOverride = PowerLevelsContent(
                eventsDefault = 100
        )
    }
}
