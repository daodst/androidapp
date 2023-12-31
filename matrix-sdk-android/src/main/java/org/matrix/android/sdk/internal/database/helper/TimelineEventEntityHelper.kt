

package org.matrix.android.sdk.internal.database.helper

import io.realm.Realm
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntityFields
import org.matrix.android.sdk.internal.database.query.where

internal fun TimelineEventEntity.Companion.nextId(realm: Realm): Long {
    val currentIdNum = TimelineEventEntity.where(realm).max(TimelineEventEntityFields.LOCAL_ID)
    return if (currentIdNum == null) {
        1
    } else {
        currentIdNum.toLong() + 1
    }
}

internal fun TimelineEventEntity.isMoreRecentThan(eventToCheck: TimelineEventEntity): Boolean {
    val currentChunk = this.chunk?.first(null) ?: return false
    val chunkToCheck = eventToCheck.chunk?.first(null) ?: return false
    return if (currentChunk == chunkToCheck) {
        this.displayIndex >= eventToCheck.displayIndex
    } else {
        currentChunk.isMoreRecentThan(chunkToCheck)
    }
}
