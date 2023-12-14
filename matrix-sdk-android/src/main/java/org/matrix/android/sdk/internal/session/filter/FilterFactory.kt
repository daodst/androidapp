

package org.matrix.android.sdk.internal.session.filter

import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.RelationType
import timber.log.Timber

internal object FilterFactory {

    fun createThreadsFilter(numberOfEvents: Int, userId: String?): RoomEventFilter {
        Timber.i("$userId")
        return RoomEventFilter(
                limit = numberOfEvents,
                relationTypes = listOf(RelationType.THREAD)
        )
    }

    fun createUploadsFilter(numberOfEvents: Int): RoomEventFilter {
        return RoomEventFilter(
                limit = numberOfEvents,
                containsUrl = true,
                types = listOf(EventType.MESSAGE),
                lazyLoadMembers = true
        )
    }

    fun createDefaultFilter(): Filter {
        return FilterUtil.enableLazyLoading(Filter(), true)
    }

    fun createElementFilter(): Filter {
        return Filter(
                room = RoomFilter(
                        timeline = createElementTimelineFilter(),
                        state = createElementStateFilter()
                )
        )
    }

    fun createDefaultRoomFilter(): RoomEventFilter {
        return RoomEventFilter(
                lazyLoadMembers = true
        )
    }

    fun createElementRoomFilter(): RoomEventFilter {
        return RoomEventFilter(
                lazyLoadMembers = true
                
                
        )
    }

    private fun createElementTimelineFilter(): RoomEventFilter? {
        return null 
        
        
        
    }

    private fun createElementStateFilter(): RoomEventFilter {
        return RoomEventFilter(
                lazyLoadMembers = true
        )
    }

    
    private val listOfSupportedEventTypes = listOf(
            
            EventType.MESSAGE
    )

    
    private val listOfSupportedStateEventTypes = listOf(
            
            EventType.STATE_ROOM_MEMBER
    )
}
