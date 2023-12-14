

package org.matrix.android.sdk.api.session.space

import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.space.model.SpaceChildContent

interface Space {

    fun asRoom(): Room

    val spaceId: String

    
    fun spaceSummary(): RoomSummary?

    suspend fun addChildren(roomId: String,
                            viaServers: List<String>?,
                            order: String?,
                            suggested: Boolean? = false)

    fun getChildInfo(roomId: String): SpaceChildContent?

    suspend fun removeChildren(roomId: String)

    @Throws
    suspend fun setChildrenOrder(roomId: String, order: String?)


    @Throws
    suspend fun setChildrenSuggested(roomId: String, suggested: Boolean)

}
