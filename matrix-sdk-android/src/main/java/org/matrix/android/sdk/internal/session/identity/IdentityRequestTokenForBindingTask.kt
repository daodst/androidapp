

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.api.session.identity.IdentityServiceError
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.identity.getCountryCode
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.identity.data.IdentityPendingBinding
import org.matrix.android.sdk.internal.session.identity.data.IdentityStore
import org.matrix.android.sdk.internal.session.identity.model.IdentityRequestTokenForEmailBody
import org.matrix.android.sdk.internal.session.identity.model.IdentityRequestTokenForMsisdnBody
import org.matrix.android.sdk.internal.task.Task
import java.util.UUID
import javax.inject.Inject

internal interface IdentityRequestTokenForBindingTask : Task<IdentityRequestTokenForBindingTask.Params, Unit> {
    data class Params(
            val threePid: ThreePid,
            
            val sendAgain: Boolean
    )
}

internal class DefaultIdentityRequestTokenForBindingTask @Inject constructor(
        private val identityApiProvider: IdentityApiProvider,
        private val identityStore: IdentityStore,
        @UserId private val userId: String
) : IdentityRequestTokenForBindingTask {

    override suspend fun execute(params: IdentityRequestTokenForBindingTask.Params) {
        val identityAPI = getIdentityApiAndEnsureTerms(identityApiProvider, userId)

        val identityPendingBinding = identityStore.getPendingBinding(params.threePid)

        if (params.sendAgain && identityPendingBinding == null) {
            throw IdentityServiceError.NoCurrentBindingError
        }

        val clientSecret = identityPendingBinding?.clientSecret ?: UUID.randomUUID().toString()
        val sendAttempt = identityPendingBinding?.sendAttempt?.inc() ?: 1

        val tokenResponse = executeRequest(null) {
            when (params.threePid) {
                is ThreePid.Email  -> identityAPI.requestTokenToBindEmail(IdentityRequestTokenForEmailBody(
                        clientSecret = clientSecret,
                        sendAttempt = sendAttempt,
                        email = params.threePid.email
                ))
                is ThreePid.Msisdn -> {
                    identityAPI.requestTokenToBindMsisdn(IdentityRequestTokenForMsisdnBody(
                            clientSecret = clientSecret,
                            sendAttempt = sendAttempt,
                            phoneNumber = params.threePid.msisdn,
                            countryCode = params.threePid.getCountryCode()
                    ))
                }
            }
        }

        
        identityStore.storePendingBinding(
                params.threePid,
                IdentityPendingBinding(
                        clientSecret = clientSecret,
                        sendAttempt = sendAttempt,
                        sid = tokenResponse.sid
                )
        )
    }
}
