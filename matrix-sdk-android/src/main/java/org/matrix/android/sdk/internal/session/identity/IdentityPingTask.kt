

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.identity.IdentityServiceError
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

internal interface IdentityPingTask : Task<IdentityPingTask.Params, Unit> {
    data class Params(
            val identityAuthAPI: IdentityAuthAPI
    )
}

internal class DefaultIdentityPingTask @Inject constructor() : IdentityPingTask {

    override suspend fun execute(params: IdentityPingTask.Params) {
        try {
            executeRequest(null) {
                params.identityAuthAPI.ping()
            }
        } catch (throwable: Throwable) {
            if (throwable is Failure.ServerError && throwable.httpCode == HttpsURLConnection.HTTP_NOT_FOUND ) {
                
                executeRequest(null) {
                    params.identityAuthAPI.pingV1()
                }
                
                throw IdentityServiceError.OutdatedIdentityServer
            } else {
                throw throwable
            }
        }
    }
}
