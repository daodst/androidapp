

package org.matrix.android.sdk.api.session.space

import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo

data class SpaceHierarchyData(
        val rootSummary: RoomSummary,
        val children: List<SpaceChildInfo>,
        val childrenState: List<Event>,
        val nextToken: String? = null
)
