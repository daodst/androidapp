

package org.matrix.android.sdk.api.session.room.timeline


interface Timeline {

    val timelineID: String

    val isLive: Boolean

    fun addListener(listener: Listener): Boolean

    fun removeListener(listener: Listener): Boolean

    fun removeAllListeners()

    
    fun start(rootThreadEventId: String? = null, isSearch: Boolean = false)

    
    fun dispose()

    
    fun restartWithEventId(eventId: String?)

    
    fun hasMoreToLoad(direction: Direction): Boolean

    
    fun paginate(direction: Direction, count: Int)

    
    suspend fun awaitPaginate(direction: Direction, count: Int): List<TimelineEvent>

    
    fun getIndexOfEvent(eventId: String?): Int?

    
    fun getPaginationState(direction: Direction): PaginationState

    
    fun getSnapshot(): List<TimelineEvent>

    interface Listener {
        
        fun onTimelineUpdated(snapshot: List<TimelineEvent>) = Unit

        fun onFindAll(snapshot: List<TimelineEvent>) = Unit

        
        fun onTimelineFailure(throwable: Throwable) = Unit

        
        fun onNewTimelineEvents(eventIds: List<String>) = Unit

        
        fun onStateUpdated(direction: Direction, state: PaginationState) = Unit
    }

    
    data class PaginationState(
            val hasMoreToLoad: Boolean = true,
            val loading: Boolean = false,
            val inError: Boolean = false,
            val failCount : Int = 0
    )

    
    enum class Direction {
        
        FORWARDS,

        
        BACKWARDS
    }
}
