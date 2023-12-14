
package org.matrix.android.sdk.internal.crypto.model

import org.matrix.android.sdk.api.session.crypto.crosssigning.CryptoCrossSigningKey
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.internal.crypto.model.rest.DeviceKeys
import org.matrix.android.sdk.internal.crypto.model.rest.DeviceKeysWithUnsigned
import org.matrix.android.sdk.internal.crypto.model.rest.RestKeyInfo

internal object CryptoInfoMapper {

    fun map(deviceKeysWithUnsigned: DeviceKeysWithUnsigned): CryptoDeviceInfo {
        return CryptoDeviceInfo(
                deviceId = deviceKeysWithUnsigned.deviceId,
                userId = deviceKeysWithUnsigned.userId,
                algorithms = deviceKeysWithUnsigned.algorithms,
                keys = deviceKeysWithUnsigned.keys,
                signatures = deviceKeysWithUnsigned.signatures,
                unsigned = deviceKeysWithUnsigned.unsigned,
                trustLevel = null
        )
    }

    fun map(cryptoDeviceInfo: CryptoDeviceInfo): DeviceKeys {
        return DeviceKeys(
                deviceId = cryptoDeviceInfo.deviceId,
                algorithms = cryptoDeviceInfo.algorithms,
                keys = cryptoDeviceInfo.keys,
                signatures = cryptoDeviceInfo.signatures,
                userId = cryptoDeviceInfo.userId
        )
    }

    fun map(keyInfo: RestKeyInfo): CryptoCrossSigningKey {
        return CryptoCrossSigningKey(
                userId = keyInfo.userId,
                usages = keyInfo.usages,
                keys = keyInfo.keys.orEmpty(),
                signatures = keyInfo.signatures,
                trustLevel = null
        )
    }

    fun map(keyInfo: CryptoCrossSigningKey): RestKeyInfo {
        return RestKeyInfo(
                userId = keyInfo.userId,
                usages = keyInfo.usages,
                keys = keyInfo.keys,
                signatures = keyInfo.signatures
        )
    }
}
