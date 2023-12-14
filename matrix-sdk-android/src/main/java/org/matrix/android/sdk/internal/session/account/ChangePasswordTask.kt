

package org.matrix.android.sdk.internal.session.account

import org.matrix.android.sdk.api.failure.toRegistrationFlowResponse
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface ChangePasswordTask : Task<ChangePasswordTask.Params, Unit> {
    data class Params(
            val password: String,
            val newPassword: String
    )
}

internal class DefaultChangePasswordTask @Inject constructor(
        private val accountAPI: AccountAPI,
        private val globalErrorReceiver: GlobalErrorReceiver,
        @UserId private val userId: String
) : ChangePasswordTask {

    override suspend fun execute(params: ChangePasswordTask.Params) {
        val changePasswordParams = ChangePasswordParams.create(userId, params.password, params.newPassword)
        try {
            executeRequest(globalErrorReceiver) {
                accountAPI.changePassword(changePasswordParams)
            }
        } catch (throwable: Throwable) {
            val registrationFlowResponse = throwable.toRegistrationFlowResponse()

            if (registrationFlowResponse != null &&
                    
                    changePasswordParams.auth?.session == null) {
                
                executeRequest(globalErrorReceiver) {
                    accountAPI.changePassword(
                            changePasswordParams.copy(auth = changePasswordParams.auth?.copy(session = registrationFlowResponse.session))
                    )
                }
            } else {
                throw throwable
            }
        }
    }
}
