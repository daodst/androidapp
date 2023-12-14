

package org.matrix.android.sdk.api.session.homeserver

data class RoomVersionCapabilities(
        val defaultRoomVersion: String,
        val supportedVersion: List<RoomVersionInfo>,
        
        val capabilities: Map<String, RoomCapabilitySupport>?
)

data class RoomVersionInfo(
        val version: String,
        val status: RoomVersionStatus
)

data class RoomCapabilitySupport(
        val preferred: String?,
        val support: List<String>
)

enum class RoomVersionStatus {
    STABLE,
    UNSTABLE
}
