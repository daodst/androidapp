

package org.matrix.android.sdk.internal.database.mapper

import org.matrix.android.sdk.api.session.group.model.GroupSummary
import org.matrix.android.sdk.internal.database.model.GroupSummaryEntity

internal object GroupSummaryMapper {

    fun map(groupSummaryEntity: GroupSummaryEntity): GroupSummary {
        return GroupSummary(
                groupSummaryEntity.groupId,
                groupSummaryEntity.membership,
                groupSummaryEntity.displayName,
                groupSummaryEntity.shortDescription,
                groupSummaryEntity.avatarUrl,
                groupSummaryEntity.roomIds.toList(),
                groupSummaryEntity.userIds.toList()
        )
    }
}

internal fun GroupSummaryEntity.asDomain(): GroupSummary {
    return GroupSummaryMapper.map(this)
}
