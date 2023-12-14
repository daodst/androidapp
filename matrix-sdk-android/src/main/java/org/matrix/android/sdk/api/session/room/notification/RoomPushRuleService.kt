

package org.matrix.android.sdk.api.session.room.notification

import androidx.lifecycle.LiveData

interface RoomPushRuleService {

    fun getLiveRoomNotificationState(): LiveData<RoomNotificationState>

    suspend fun setRoomNotificationState(roomNotificationState: RoomNotificationState)
}
