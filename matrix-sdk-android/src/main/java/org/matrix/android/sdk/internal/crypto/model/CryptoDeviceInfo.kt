
package org.matrix.android.sdk.internal.crypto.model

import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.internal.crypto.model.rest.DeviceKeys

internal fun CryptoDeviceInfo.toRest(): DeviceKeys {
    return CryptoInfoMapper.map(this)
}
