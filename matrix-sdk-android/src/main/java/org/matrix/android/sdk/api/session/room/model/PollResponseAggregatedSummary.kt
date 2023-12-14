
package org.matrix.android.sdk.api.session.room.model

data class PollResponseAggregatedSummary(
        val aggregatedContent: PollSummaryContent? = null,
        
        val closedTime: Long? = null,
        
        val nbOptions: Int = 0,
        
        val sourceEvents: List<String>,
        val localEchos: List<String>
)
