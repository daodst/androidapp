

package org.matrix.android.sdk.api.session.group.model

import org.matrix.android.sdk.api.session.room.model.Membership


data class GroupSummary(
        val groupId: String,
        val membership: Membership,
        val displayName: String = "",
        val shortDescription: String = "",
        val avatarUrl: String = "",
        val roomIds: List<String> = emptyList(),
        val userIds: List<String> = emptyList()
)
