

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.api.session.identity.IdentityServiceError
import org.matrix.android.sdk.internal.network.executeRequest

internal suspend fun getIdentityApiAndEnsureTerms(identityApiProvider: IdentityApiProvider, userId: String): IdentityAPI {
    val identityAPI = identityApiProvider.identityApi ?: throw IdentityServiceError.NoIdentityServerConfigured

    
    val identityAccountResponse = executeRequest(null) {
        identityAPI.getAccount()
    }

    assert(userId == identityAccountResponse.userId)

    return identityAPI
}
