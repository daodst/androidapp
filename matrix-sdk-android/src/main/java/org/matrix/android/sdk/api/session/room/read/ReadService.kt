

package org.matrix.android.sdk.api.session.room.read

import androidx.lifecycle.LiveData
import org.matrix.android.sdk.api.session.room.model.ReadReceipt
import org.matrix.android.sdk.api.util.Optional


interface ReadService {

    enum class MarkAsReadParams {
        READ_RECEIPT,
        READ_MARKER,
        BOTH
    }

    
    suspend fun markAsRead(params: MarkAsReadParams = MarkAsReadParams.BOTH)

    
    suspend fun setReadReceipt(eventId: String)

    
    suspend fun setReadMarker(fullyReadEventId: String)

    
    fun isEventRead(eventId: String): Boolean

    
    fun getReadMarkerLive(): LiveData<Optional<String>>

    
    fun getMyReadReceiptLive(): LiveData<Optional<String>>

    
    fun getUserReadReceipt(userId: String): String?

    
    fun getEventReadReceiptsLive(eventId: String): LiveData<List<ReadReceipt>>
}
