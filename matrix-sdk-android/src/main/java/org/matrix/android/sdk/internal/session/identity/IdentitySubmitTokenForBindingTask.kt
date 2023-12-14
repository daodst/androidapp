

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.api.session.identity.IdentityServiceError
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.identity.toMedium
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.identity.data.IdentityStore
import org.matrix.android.sdk.internal.session.identity.model.IdentityRequestOwnershipParams
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface IdentitySubmitTokenForBindingTask : Task<IdentitySubmitTokenForBindingTask.Params, Unit> {
    data class Params(
            val threePid: ThreePid,
            val token: String
    )
}

internal class DefaultIdentitySubmitTokenForBindingTask @Inject constructor(
        private val identityApiProvider: IdentityApiProvider,
        private val identityStore: IdentityStore,
        @UserId private val userId: String
) : IdentitySubmitTokenForBindingTask {

    override suspend fun execute(params: IdentitySubmitTokenForBindingTask.Params) {
        val identityAPI = getIdentityApiAndEnsureTerms(identityApiProvider, userId)
        val identityPendingBinding = identityStore.getPendingBinding(params.threePid) ?: throw IdentityServiceError.NoCurrentBindingError

        val tokenResponse = executeRequest(null) {
            identityAPI.submitToken(
                    params.threePid.toMedium(),
                    IdentityRequestOwnershipParams(
                            clientSecret = identityPendingBinding.clientSecret,
                            sid = identityPendingBinding.sid,
                            token = params.token
                    ))
        }

        if (!tokenResponse.isSuccess()) {
            throw IdentityServiceError.BindingError
        }
    }
}
