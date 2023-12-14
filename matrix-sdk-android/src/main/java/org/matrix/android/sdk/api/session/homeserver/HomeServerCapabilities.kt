

package org.matrix.android.sdk.api.session.homeserver

data class HomeServerCapabilities(
        
        val canChangePassword: Boolean = true,
        
        val canChangeDisplayName: Boolean = true,
        
        val canChangeAvatar: Boolean = true,
        
        val canChange3pid: Boolean = true,
        
        val maxUploadFileSize: Long = MAX_UPLOAD_FILE_SIZE_UNKNOWN,
        
        val lastVersionIdentityServerSupported: Boolean = false,
        
        val defaultIdentityServerUrl: String? = null,
        
        val roomVersions: RoomVersionCapabilities? = null,
        
        val canUseThreading: Boolean = false
) {

    enum class RoomCapabilitySupport {
        SUPPORTED,
        SUPPORTED_UNSTABLE,
        UNSUPPORTED,
        UNKNOWN
    }

    
    fun isFeatureSupported(feature: String): RoomCapabilitySupport {
        if (roomVersions?.capabilities == null) return RoomCapabilitySupport.UNKNOWN
        val info = roomVersions.capabilities[feature] ?: return RoomCapabilitySupport.UNSUPPORTED

        val preferred = info.preferred ?: info.support.lastOrNull()
        val versionCap = roomVersions.supportedVersion.firstOrNull { it.version == preferred }

        return when {
            versionCap == null                            -> {
                RoomCapabilitySupport.UNKNOWN
            }
            versionCap.status == RoomVersionStatus.STABLE -> {
                RoomCapabilitySupport.SUPPORTED
            }
            else                                          -> {
                RoomCapabilitySupport.SUPPORTED_UNSTABLE
            }
        }
    }

    fun isFeatureSupported(feature: String, byRoomVersion: String): Boolean {
        if (roomVersions?.capabilities == null) return false
        val info = roomVersions.capabilities[feature] ?: return false

        return info.preferred == byRoomVersion || info.support.contains(byRoomVersion)
    }

    
    fun versionOverrideForFeature(feature: String): String? {
        val cap = roomVersions?.capabilities?.get(feature)
        return cap?.preferred ?: cap?.support?.lastOrNull()
    }

    companion object {
        const val MAX_UPLOAD_FILE_SIZE_UNKNOWN = -1L
        const val ROOM_CAP_KNOCK = "knock"
        const val ROOM_CAP_RESTRICTED = "restricted"
    }
}
