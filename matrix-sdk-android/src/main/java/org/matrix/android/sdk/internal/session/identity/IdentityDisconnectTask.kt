

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.api.session.identity.IdentityServiceError
import org.matrix.android.sdk.internal.di.AuthenticatedIdentity
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.network.token.AccessTokenProvider
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber
import javax.inject.Inject

internal interface IdentityDisconnectTask : Task<Unit, Unit>

internal class DefaultIdentityDisconnectTask @Inject constructor(
        private val identityApiProvider: IdentityApiProvider,
        @AuthenticatedIdentity
        private val accessTokenProvider: AccessTokenProvider
) : IdentityDisconnectTask {

    override suspend fun execute(params: Unit) {
        val identityAPI = identityApiProvider.identityApi ?: throw IdentityServiceError.NoIdentityServerConfigured

        
        
        if (accessTokenProvider.getToken() == null) {
            Timber.d("No token to disconnect identity server.")
            return
        }

        executeRequest(null) {
            identityAPI.logout()
        }
    }
}
