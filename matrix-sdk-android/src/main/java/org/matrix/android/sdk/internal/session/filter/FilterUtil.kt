

package org.matrix.android.sdk.internal.session.filter

internal object FilterUtil {

    
    

    
    fun enableLazyLoading(filter: Filter, useLazyLoading: Boolean): Filter {
        if (useLazyLoading) {
            
            return filter.copy(
                    room = filter.room?.copy(
                            state = filter.room.state?.copy(lazyLoadMembers = true)
                                    ?: RoomEventFilter(lazyLoadMembers = true)
                    )
                            ?: RoomFilter(state = RoomEventFilter(lazyLoadMembers = true))
            )
        } else {
            val newRoomEventFilter = filter.room?.state?.copy(lazyLoadMembers = null)?.takeIf { it.hasData() }
            val newRoomFilter = filter.room?.copy(state = newRoomEventFilter)?.takeIf { it.hasData() }

            return filter.copy(
                    room = newRoomFilter
            )
        }
    }
}
