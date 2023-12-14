

package org.matrix.android.sdk.internal.auth.registration

import org.matrix.android.sdk.internal.auth.AuthAPI
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task

internal interface ValidateCodeTask : Task<ValidateCodeTask.Params, SuccessResult> {
    data class Params(
            val url: String,
            val body: ValidationCodeBody
    )
}

internal class DefaultValidateCodeTask(
        private val authAPI: AuthAPI
) : ValidateCodeTask {

    override suspend fun execute(params: ValidateCodeTask.Params): SuccessResult {
        return executeRequest(null) {
            authAPI.validate3Pid(params.url, params.body)
        }
    }
}
