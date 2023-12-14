

package org.matrix.android.sdk.api.session.permalinks

import org.matrix.android.sdk.api.session.events.model.Event


interface PermalinkService {

    companion object {
        
        const val MATRIX_TO_URL_BASE = "https://www.daodst.com/#/"
    }

    enum class SpanTemplateType {
        HTML,
        MARKDOWN
    }

    
    fun createPermalink(event: Event, forceMatrixTo: Boolean = false): String?

    
    fun createPermalink(id: String, forceMatrixTo: Boolean = false): String?

    
    fun createRoomPermalink(roomId: String, viaServers: List<String>? = null, forceMatrixTo: Boolean = false): String?

    
    fun createPermalink(roomId: String, eventId: String, forceMatrixTo: Boolean = false): String

    
    fun getLinkedId(url: String): String?

    
    fun createMentionSpanTemplate(type: SpanTemplateType, forceMatrixTo: Boolean = false): String
}
