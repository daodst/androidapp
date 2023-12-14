
package org.matrix.android.sdk.api.session.crypto.crosssigning

data class DeviceTrustLevel(
        val crossSigningVerified: Boolean,
        val locallyVerified: Boolean?
) {
    fun isVerified() = crossSigningVerified || locallyVerified == true
    fun isCrossSigningVerified() = crossSigningVerified
    fun isLocallyVerified() = locallyVerified
}
