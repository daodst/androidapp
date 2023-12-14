

package org.matrix.android.sdk.internal.session.room.version

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.homeserver.RoomVersionStatus
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.create.RoomCreateContent
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.version.RoomVersionService
import org.matrix.android.sdk.internal.session.homeserver.HomeServerCapabilitiesDataSource
import org.matrix.android.sdk.internal.session.room.state.StateEventDataSource

internal class DefaultRoomVersionService @AssistedInject constructor(
        @Assisted private val roomId: String,
        private val homeServerCapabilitiesDataSource: HomeServerCapabilitiesDataSource,
        private val stateEventDataSource: StateEventDataSource,
        private val roomVersionUpgradeTask: RoomVersionUpgradeTask
) : RoomVersionService {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): DefaultRoomVersionService
    }

    override fun getRoomVersion(): String {
        return stateEventDataSource.getStateEvent(roomId, EventType.STATE_ROOM_CREATE, QueryStringValue.IsEmpty)
                ?.content
                ?.toModel<RoomCreateContent>()
                ?.roomVersion
        
                ?: DEFAULT_ROOM_VERSION
    }

    override suspend fun upgradeToVersion(version: String): String {
        return roomVersionUpgradeTask.execute(
                RoomVersionUpgradeTask.Params(
                        roomId = roomId,
                        newVersion = version
                )
        )
    }

    override fun getRecommendedVersion(): String {
        return homeServerCapabilitiesDataSource.getHomeServerCapabilities()?.roomVersions?.defaultRoomVersion ?: DEFAULT_ROOM_VERSION
    }

    override fun isUsingUnstableRoomVersion(): Boolean {
        val versionCaps = homeServerCapabilitiesDataSource.getHomeServerCapabilities()?.roomVersions
        val currentVersion = getRoomVersion()
        return versionCaps?.supportedVersion?.firstOrNull { it.version == currentVersion }?.status == RoomVersionStatus.UNSTABLE
    }

    override fun userMayUpgradeRoom(userId: String): Boolean {
        val powerLevelsHelper = stateEventDataSource.getStateEvent(roomId, EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.NoCondition)
                ?.content?.toModel<PowerLevelsContent>()
                ?.let { PowerLevelsHelper(it) }

        return powerLevelsHelper?.isUserAllowedToSend(userId, true, EventType.STATE_ROOM_TOMBSTONE) ?: false
    }

    companion object {
        const val DEFAULT_ROOM_VERSION = "1"
    }
}
