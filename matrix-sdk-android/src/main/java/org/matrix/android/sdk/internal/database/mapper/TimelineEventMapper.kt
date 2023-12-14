

package org.matrix.android.sdk.internal.database.mapper

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.model.RemarkEntity
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.query.where
import javax.inject.Inject

internal class TimelineEventMapper @Inject constructor(private val readReceiptsSummaryMapper: ReadReceiptsSummaryMapper,
                                                       private val realmSessionProvider: RealmSessionProvider) {

    fun map(timelineEventEntity: TimelineEventEntity, buildReadReceipts: Boolean = true): TimelineEvent {
        val readReceipts = if (buildReadReceipts) {
            timelineEventEntity.readReceipts
                    ?.let {
                        readReceiptsSummaryMapper.map(it)
                    }
        } else {
            null
        }
        var remark: String? = null;
        timelineEventEntity.root?.sender?.let { userId ->
            remark = realmSessionProvider.withRealm {
                RemarkEntity.where(it, userId).findFirst()?.remark
            }
        }

        return TimelineEvent(
                root = timelineEventEntity.root?.asDomain()
                        ?: Event("", timelineEventEntity.eventId),
                eventId = timelineEventEntity.eventId,
                annotations = timelineEventEntity.annotations?.asDomain(),
                localId = timelineEventEntity.localId,
                displayIndex = timelineEventEntity.displayIndex,
                senderInfo = SenderInfo(
                        userId = timelineEventEntity.root?.sender ?: "",
                        displayName = timelineEventEntity.senderName,
                        isUniqueDisplayName = timelineEventEntity.isUniqueDisplayName,
                        avatarUrl = timelineEventEntity.senderAvatar,
                        remark
                ),
                ownedByThreadChunk = timelineEventEntity.ownedByThreadChunk,
                readReceipts = readReceipts
                        ?.distinctBy {
                            it.roomMember
                        }?.sortedByDescending {
                            it.originServerTs
                        }.orEmpty()
        )
    }
}
