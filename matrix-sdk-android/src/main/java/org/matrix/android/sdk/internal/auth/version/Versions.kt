

package org.matrix.android.sdk.internal.auth.version

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class Versions(
        @Json(name = "versions")
        val supportedVersions: List<String>? = null,

        @Json(name = "unstable_features")
        val unstableFeatures: Map<String, Boolean>? = null
)

private const val FEATURE_LAZY_LOAD_MEMBERS = "m.lazy_load_members"
private const val FEATURE_REQUIRE_IDENTITY_SERVER = "m.require_identity_server"
private const val FEATURE_ID_ACCESS_TOKEN = "m.id_access_token"
private const val FEATURE_SEPARATE_ADD_AND_BIND = "m.separate_add_and_bind"
private const val FEATURE_THREADS_MSC3440 = "org.matrix.msc3440"
private const val FEATURE_THREADS_MSC3440_STABLE = "org.matrix.msc3440.stable"


internal fun Versions.isSupportedBySdk(): Boolean {
    return supportLazyLoadMembers()
}


internal fun Versions.isLoginAndRegistrationSupportedBySdk(): Boolean {
    return !doesServerRequireIdentityServerParam() &&
            doesServerAcceptIdentityAccessToken() &&
            doesServerSeparatesAddAndBind()
}


internal fun Versions.doesServerSupportThreads(): Boolean {
    return getMaxVersion() >= HomeServerVersion.v1_3_0 ||
            unstableFeatures?.get(FEATURE_THREADS_MSC3440_STABLE) ?: false
}


private fun Versions.supportLazyLoadMembers(): Boolean {
    return getMaxVersion() >= HomeServerVersion.r0_5_0 ||
            unstableFeatures?.get(FEATURE_LAZY_LOAD_MEMBERS) == true
}


private fun Versions.doesServerRequireIdentityServerParam(): Boolean {
    if (getMaxVersion() >= HomeServerVersion.r0_6_0) return false
    return unstableFeatures?.get(FEATURE_REQUIRE_IDENTITY_SERVER) ?: true
}


private fun Versions.doesServerAcceptIdentityAccessToken(): Boolean {
    return getMaxVersion() >= HomeServerVersion.r0_6_0 ||
            unstableFeatures?.get(FEATURE_ID_ACCESS_TOKEN) ?: false
}

private fun Versions.doesServerSeparatesAddAndBind(): Boolean {
    return getMaxVersion() >= HomeServerVersion.r0_6_0 ||
            unstableFeatures?.get(FEATURE_SEPARATE_ADD_AND_BIND) ?: false
}

private fun Versions.getMaxVersion(): HomeServerVersion {
    return supportedVersions
            ?.mapNotNull { HomeServerVersion.parse(it) }
            ?.maxOrNull()
            ?: HomeServerVersion.r0_0_0
}
