

package org.matrix.android.sdk.api.session.search

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.util.MatrixItem


data class SearchResult(
    
    val nextBatch: String? = null,
    
    val highlights: List<String>? = null,
    
    val results: List<EventAndSender>? = null
)

data class EventAndSender(
    val event: Event,
    val sender: MatrixItem.UserItem?,
    var content: MessageContent?,
    var body: String = ""
)
