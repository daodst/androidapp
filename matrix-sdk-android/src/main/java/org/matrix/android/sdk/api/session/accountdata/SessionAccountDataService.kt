

package org.matrix.android.sdk.api.session.accountdata

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.accountdata.RoomAccountDataEvent
import org.matrix.android.sdk.api.util.Optional


interface SessionAccountDataService {
    
    fun getUserAccountDataEvent(type: String): UserAccountDataEvent?

    
    fun getLiveUserAccountDataEvent(type: String): LiveData<Optional<UserAccountDataEvent>>

    
    fun getUserAccountDataEvents(types: Set<String>): List<UserAccountDataEvent>

    
    fun getLiveUserAccountDataEvents(types: Set<String>): LiveData<List<UserAccountDataEvent>>

    
    fun getRoomAccountDataEvents(types: Set<String>): List<RoomAccountDataEvent>

    
    fun getLiveRoomAccountDataEvents(types: Set<String>): LiveData<List<RoomAccountDataEvent>>

    
    suspend fun updateUserAccountData(type: String, content: Content)
}
