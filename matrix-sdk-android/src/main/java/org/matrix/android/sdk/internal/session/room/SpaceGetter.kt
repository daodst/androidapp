

package org.matrix.android.sdk.internal.session.room

import org.matrix.android.sdk.api.session.space.Space
import javax.inject.Inject

internal interface SpaceGetter {
    fun get(spaceId: String): Space?
}

internal class DefaultSpaceGetter @Inject constructor(
        private val roomGetter: RoomGetter
) : SpaceGetter {

    override fun get(spaceId: String): Space? {
        return roomGetter.getRoom(spaceId)?.asSpace()
    }
}
