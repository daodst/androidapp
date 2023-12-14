

package org.matrix.android.sdk.internal.session.signout

import org.matrix.android.sdk.api.auth.data.SessionParams
import org.matrix.android.sdk.internal.auth.SessionParamsStore
import org.matrix.android.sdk.internal.auth.data.PasswordLoginParams
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface SignInAgainTask : Task<SignInAgainTask.Params, Unit> {
    data class Params(
            val password: String
    )
}

internal class DefaultSignInAgainTask @Inject constructor(
        private val signOutAPI: SignOutAPI,
        private val sessionParams: SessionParams,
        private val sessionParamsStore: SessionParamsStore,
        private val globalErrorReceiver: GlobalErrorReceiver
) : SignInAgainTask {

    override suspend fun execute(params: SignInAgainTask.Params) {
        val newCredentials = executeRequest(globalErrorReceiver) {
            signOutAPI.loginAgain(
                    PasswordLoginParams.userIdentifier(
                            
                            user = sessionParams.userId,
                            password = params.password,
                            
                            
                            
                            deviceDisplayName = null,
                            
                            deviceId = sessionParams.deviceId
                    )
            )
        }

        sessionParamsStore.updateCredentials(newCredentials)
    }
}
