

package org.matrix.android.sdk.internal.session.profile

import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.internal.auth.registration.ValidationCodeBody
import org.matrix.android.sdk.internal.database.model.PendingThreePidEntity
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface ValidateSmsCodeTask : Task<ValidateSmsCodeTask.Params, Unit> {
    data class Params(
            val threePid: ThreePid.Msisdn,
            val code: String
    )
}

internal class DefaultValidateSmsCodeTask @Inject constructor(
        private val profileAPI: ProfileAPI,
        @SessionDatabase
        private val monarchy: Monarchy,
        private val pendingThreePidMapper: PendingThreePidMapper,
        private val globalErrorReceiver: GlobalErrorReceiver
) : ValidateSmsCodeTask {

    override suspend fun execute(params: ValidateSmsCodeTask.Params) {
        
        val pendingThreePids = monarchy.fetchAllMappedSync(
                { it.where(PendingThreePidEntity::class.java) },
                { pendingThreePidMapper.map(it) }
        )
                .firstOrNull { it.threePid == params.threePid }
                ?: throw IllegalArgumentException("unknown threepid")

        val url = pendingThreePids.submitUrl ?: throw IllegalArgumentException("invalid threepid")
        val body = ValidationCodeBody(
                clientSecret = pendingThreePids.clientSecret,
                sid = pendingThreePids.sid,
                code = params.code
        )
        val result = executeRequest(globalErrorReceiver) {
            profileAPI.validateMsisdn(url, body)
        }

        if (!result.isSuccess()) {
            throw Failure.SuccessError
        }
    }
}
