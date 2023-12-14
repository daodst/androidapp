

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.api.session.openid.OpenIdToken
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.identity.model.IdentityRegisterResponse
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface IdentityRegisterTask : Task<IdentityRegisterTask.Params, IdentityRegisterResponse> {
    data class Params(
            val identityAuthAPI: IdentityAuthAPI,
            val openIdToken: OpenIdToken
    )
}

internal class DefaultIdentityRegisterTask @Inject constructor() : IdentityRegisterTask {

    override suspend fun execute(params: IdentityRegisterTask.Params): IdentityRegisterResponse {
        return executeRequest(null) {
            params.identityAuthAPI.register(params.openIdToken)
        }
    }
}
