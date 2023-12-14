
package org.matrix.android.sdk.api.session.room.model

import org.matrix.android.sdk.api.session.events.model.Content

data class EditAggregatedSummary(
        val latestContent: Content? = null,
        
        val sourceEvents: List<String>,
        val localEchos: List<String>,
        val lastEditTs: Long = 0
)
