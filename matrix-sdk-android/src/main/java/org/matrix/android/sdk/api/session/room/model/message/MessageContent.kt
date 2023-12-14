

package org.matrix.android.sdk.api.session.room.model.message

import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent

interface MessageContent {

    companion object {
        const val MSG_TYPE_JSON_KEY = "msgtype"
    }

    val msgType: String
    val body: String
    val relatesTo: RelationDefaultContent?
    val newContent: Content?
}
