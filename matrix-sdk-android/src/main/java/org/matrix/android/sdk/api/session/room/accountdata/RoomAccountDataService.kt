

package org.matrix.android.sdk.api.session.room.accountdata

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.util.Optional


interface RoomAccountDataService {
    
    fun getAccountDataEvent(type: String): RoomAccountDataEvent?

    
    fun getLiveAccountDataEvent(type: String): LiveData<Optional<RoomAccountDataEvent>>

    
    fun getAccountDataEvents(types: Set<String>): List<RoomAccountDataEvent>

    
    fun getLiveAccountDataEvents(types: Set<String>): LiveData<List<RoomAccountDataEvent>>

    
    suspend fun updateAccountData(type: String, content: Content)
}
