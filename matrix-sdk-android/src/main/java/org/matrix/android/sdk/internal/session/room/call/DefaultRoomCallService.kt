

package org.matrix.android.sdk.internal.session.room.call

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.room.call.RoomCallService
import org.matrix.android.sdk.internal.session.room.RoomGetter

internal class DefaultRoomCallService @AssistedInject constructor(
        @Assisted private val roomId: String,
        private val roomGetter: RoomGetter
) : RoomCallService {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): DefaultRoomCallService
    }

    override fun canStartCall(): Boolean {
        return roomGetter.getRoom(roomId)?.roomSummary()?.canStartCall.orFalse()
    }
}
