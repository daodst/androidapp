

package org.matrix.android.sdk.api.session.room.model

data class ReactionAggregatedSummary(
        val key: String,                
        val count: Int,                 
        val addedByMe: Boolean,         
        val firstTimestamp: Long,       
        val sourceEvents: List<String>,
        val localEchoEvents: List<String>
)
