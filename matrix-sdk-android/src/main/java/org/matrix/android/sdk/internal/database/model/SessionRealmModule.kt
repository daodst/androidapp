

package org.matrix.android.sdk.internal.database.model

import io.realm.annotations.RealmModule
import org.matrix.android.sdk.internal.database.model.presence.UserPresenceEntity
import org.matrix.android.sdk.internal.database.model.threads.ThreadSummaryEntity


@RealmModule(
        library = true,
        classes = [
            ChunkEntity::class,
            EventEntity::class,
            ChatPhoneLog::class,
            EventInsertEntity::class,
            TimelineEventEntity::class,
            FilterEntity::class,
            GroupEntity::class,
            GroupSummaryEntity::class,
            ReadReceiptEntity::class,
            RoomEntity::class,
            RoomSummaryEntity::class,
            RoomTagEntity::class,
            SyncEntity::class,
            PendingThreePidEntity::class,
            UserEntity::class,
            IgnoredUserEntity::class,
            BreadcrumbsEntity::class,
            UserThreePidEntity::class,
            EventAnnotationsSummaryEntity::class,
            ReactionAggregatedSummaryEntity::class,
            EditAggregatedSummaryEntity::class,
            EditionOfEvent::class,
            PollResponseAggregatedSummaryEntity::class,
            ReferencesAggregatedSummaryEntity::class,
            PushRulesEntity::class,
            PushRuleEntity::class,
            PushConditionEntity::class,
            PreviewUrlCacheEntity::class,
            PusherEntity::class,
            PusherDataEntity::class,
            ReadReceiptsSummaryEntity::class,
            ReadMarkerEntity::class,
            UserDraftsEntity::class,
            DraftEntity::class,
            HomeServerCapabilitiesEntity::class,
            RoomMemberSummaryEntity::class,
            CurrentStateEventEntity::class,
            UserAccountDataEntity::class,
            ScalarTokenEntity::class,
            WellknownIntegrationManagerConfigEntity::class,
            RoomAccountDataEntity::class,
            SpaceChildSummaryEntity::class,
            SpaceParentSummaryEntity::class,
            UserPresenceEntity::class,
            ThreadSummaryEntity::class,
            RemarkEntity::class,
            ChainEntity::class,
        ]
)
internal class SessionRealmModule
