

package org.matrix.android.sdk.internal.session.sync.handler.room

import io.realm.Realm
import org.matrix.android.sdk.api.session.room.model.tag.RoomTagContent
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.RoomTagEntity
import org.matrix.android.sdk.internal.database.query.getOrCreate
import javax.inject.Inject

internal class RoomTagHandler @Inject constructor() {

    fun handle(realm: Realm, roomId: String, content: RoomTagContent?) {
        if (content == null) {
            return
        }
        val tags = content.tags.entries.map { (tagName, params) ->
            RoomTagEntity(tagName, params["order"] as? Double)
            Pair(tagName, params["order"] as? Double)
        }
        RoomSummaryEntity.getOrCreate(realm, roomId).updateTags(tags)
    }
}
