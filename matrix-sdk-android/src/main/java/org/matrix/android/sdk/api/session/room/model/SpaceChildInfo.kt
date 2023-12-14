

package org.matrix.android.sdk.api.session.room.model

data class SpaceChildInfo(
        val childRoomId: String,
        
        
        val isKnown: Boolean,
        val roomType: String?,
        val name: String?,
        val topic: String?,
        val avatarUrl: String?,
        val order: String?,
        val activeMemberCount: Int?,
        val viaServers: List<String>,
        val parentRoomId: String?,
        val suggested: Boolean?,
        val canonicalAlias: String?,
        val aliases: List<String>?,
        val worldReadable: Boolean

)
