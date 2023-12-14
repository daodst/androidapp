

package org.matrix.android.sdk.internal.session.identity.data

internal data class IdentityData(
        val identityServerUrl: String?,
        val token: String?,
        val hashLookupPepper: String?,
        val hashLookupAlgorithm: List<String>,
        val userConsent: Boolean
)
