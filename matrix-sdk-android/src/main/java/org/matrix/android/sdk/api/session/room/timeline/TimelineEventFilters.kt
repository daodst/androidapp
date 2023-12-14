

package org.matrix.android.sdk.api.session.room.timeline

data class TimelineEventFilters(
        
        val filterEdits: Boolean = false,
        
        val filterRedacted: Boolean = false,
        
        val filterUseless: Boolean = false,
        
        val filterTypes: Boolean = false,
        
        val allowedTypes: List<EventTypeFilter> = emptyList(),

        val notAllowedTypes: List<EventTypeFilter> = emptyList()
)
