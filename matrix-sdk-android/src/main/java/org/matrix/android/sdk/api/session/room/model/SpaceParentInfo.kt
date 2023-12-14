

package org.matrix.android.sdk.api.session.room.model

data class SpaceParentInfo(
        val parentId: String?,
        val roomSummary: RoomSummary?,
        val canonical: Boolean?,
        val viaServers: List<String>
)
