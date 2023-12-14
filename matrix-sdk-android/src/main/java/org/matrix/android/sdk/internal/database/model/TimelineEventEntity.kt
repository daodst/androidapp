

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.Index
import io.realm.annotations.LinkingObjects
import org.matrix.android.sdk.internal.extensions.assertIsManaged

internal open class TimelineEventEntity(var localId: Long = 0,
                                        @Index var eventId: String = "",
                                        @Index var roomId: String = "",
                                        @Index var displayIndex: Int = 0,
                                        var root: EventEntity? = null,
                                        var annotations: EventAnnotationsSummaryEntity? = null,
                                        var senderName: String? = null,
                                        var isUniqueDisplayName: Boolean = false,
                                        var senderAvatar: String? = null,
                                        var senderMembershipEventId: String? = null,
        
        
                                        var ownedByThreadChunk: Boolean = false,
                                        var readReceipts: ReadReceiptsSummaryEntity? = null
) : RealmObject() {

    @LinkingObjects("timelineEvents")
    val chunk: RealmResults<ChunkEntity>? = null

    companion object
}

internal fun TimelineEventEntity.deleteOnCascade(canDeleteRoot: Boolean) {
    assertIsManaged()
    if (canDeleteRoot) {
        root?.deleteFromRealm()
    }
    deleteFromRealm()
}
