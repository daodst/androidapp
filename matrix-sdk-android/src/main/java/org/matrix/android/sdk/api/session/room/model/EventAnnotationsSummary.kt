
package org.matrix.android.sdk.api.session.room.model

data class EventAnnotationsSummary(
        val eventId: String,
        val reactionsSummary: List<ReactionAggregatedSummary> = emptyList(),
        val editSummary: EditAggregatedSummary? = null,
        val pollResponseSummary: PollResponseAggregatedSummary? = null,
        val referencesAggregatedSummary: ReferencesAggregatedSummary? = null
)
