

package org.matrix.android.sdk.internal.session.filter

internal interface FilterRepository {

    
    suspend fun storeFilter(filter: Filter, roomEventFilter: RoomEventFilter): Boolean

    
    suspend fun storeFilterId(filter: Filter, filterId: String)

    
    suspend fun getFilter(): String

    
    suspend fun getRoomFilter(): String
}
