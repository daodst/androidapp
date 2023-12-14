

package org.matrix.android.sdk.api.session.room.model.relation

import org.matrix.android.sdk.api.session.events.model.RelationType

interface RelationContent {
    
    val type: String?
    val eventId: String?
    val inReplyTo: ReplyToContent?
    val option: Int?

    
    val isFallingBack: Boolean?
}
