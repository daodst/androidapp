
package org.matrix.android.sdk.api.session.room.model

import org.matrix.android.sdk.api.session.events.model.Content


data class ReferencesAggregatedSummary(
        val eventId: String,
        val content: Content?,
        val sourceEvents: List<String>,
        val localEchos: List<String>
)
