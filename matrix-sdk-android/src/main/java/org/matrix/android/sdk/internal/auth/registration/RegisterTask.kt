

package org.matrix.android.sdk.internal.auth.registration

import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.toRegistrationFlowResponse
import org.matrix.android.sdk.internal.auth.AuthAPI
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import timber.log.Timber

internal interface RegisterTask : Task<RegisterTask.Params, Credentials> {
    data class Params(
            val registrationParams: RegistrationParams
    )
}

internal class DefaultRegisterTask(
        private val authAPI: AuthAPI
) : RegisterTask {

    override suspend fun execute(params: RegisterTask.Params): Credentials {
        try {
            return executeRequest(null) {
                authAPI.register(params.registrationParams)
            }
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            throw throwable.toRegistrationFlowResponse()
                    ?.let { Failure.RegistrationFlowError(it) }
                    ?: throwable
        }
    }
}
