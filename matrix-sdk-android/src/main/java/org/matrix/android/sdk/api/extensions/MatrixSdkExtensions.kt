

package org.matrix.android.sdk.api.extensions

import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo



fun CryptoDeviceInfo.getFingerprintHumanReadable() = fingerprint()
        ?.chunked(4)
        ?.joinToString(separator = " ")



fun List<DeviceInfo>.sortByLastSeen(): List<DeviceInfo> {
    return this.sortedByDescending { it.lastSeenTs ?: 0 }
}
