

package org.matrix.android.sdk.api.session.room.version

interface RoomVersionService {
    
    fun getRoomVersion(): String

    
    suspend fun upgradeToVersion(version: String): String

    
    fun getRecommendedVersion(): String

    
    fun userMayUpgradeRoom(userId: String): Boolean

    
    fun isUsingUnstableRoomVersion(): Boolean
}
