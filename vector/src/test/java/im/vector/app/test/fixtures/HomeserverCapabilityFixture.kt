

package im.vector.app.test.fixtures

import org.matrix.android.sdk.api.session.homeserver.HomeServerCapabilities
import org.matrix.android.sdk.api.session.homeserver.RoomVersionCapabilities

fun aHomeServerCapabilities(
        canChangePassword: Boolean = true,
        canChangeDisplayName: Boolean = true,
        canChangeAvatar: Boolean = true,
        canChange3pid: Boolean = true,
        maxUploadFileSize: Long = 100L,
        lastVersionIdentityServerSupported: Boolean = false,
        defaultIdentityServerUrl: String? = null,
        roomVersions: RoomVersionCapabilities? = null
) = HomeServerCapabilities(
        canChangePassword,
        canChangeDisplayName,
        canChangeAvatar,
        canChange3pid,
        maxUploadFileSize,
        lastVersionIdentityServerSupported,
        defaultIdentityServerUrl,
        roomVersions
)
