
package org.matrix.android.sdk.api.session.crypto.crosssigning

sealed class DeviceTrustResult {
    data class Success(val level: DeviceTrustLevel) : DeviceTrustResult()
    data class UnknownDevice(val deviceID: String) : DeviceTrustResult()
    data class CrossSigningNotConfigured(val userID: String) : DeviceTrustResult()
    data class KeysNotTrusted(val key: MXCrossSigningInfo) : DeviceTrustResult()
    data class MissingDeviceSignature(val deviceId: String, val signingKey: String) : DeviceTrustResult()
    data class InvalidDeviceSignature(val deviceId: String, val signingKey: String, val throwable: Throwable?) : DeviceTrustResult()
}

fun DeviceTrustResult.isSuccess() = this is DeviceTrustResult.Success
fun DeviceTrustResult.isCrossSignedVerified() = this is DeviceTrustResult.Success && level.isCrossSigningVerified()
fun DeviceTrustResult.isLocallyVerified() = this is DeviceTrustResult.Success && level.isLocallyVerified() == true
