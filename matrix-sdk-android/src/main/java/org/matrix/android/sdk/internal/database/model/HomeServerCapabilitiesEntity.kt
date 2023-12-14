

package org.matrix.android.sdk.internal.database.model

import io.realm.RealmObject
import org.matrix.android.sdk.api.session.homeserver.HomeServerCapabilities

internal open class HomeServerCapabilitiesEntity(
        var canChangePassword: Boolean = true,
        var canChangeDisplayName: Boolean = true,
        var canChangeAvatar: Boolean = true,
        var canChange3pid: Boolean = true,
        var roomVersionsJson: String? = null,
        var maxUploadFileSize: Long = HomeServerCapabilities.MAX_UPLOAD_FILE_SIZE_UNKNOWN,
        var lastVersionIdentityServerSupported: Boolean = false,
        var defaultIdentityServerUrl: String? = null,
        var lastUpdatedTimestamp: Long = 0L,
        var canUseThreading: Boolean = false
) : RealmObject() {

    companion object
}
