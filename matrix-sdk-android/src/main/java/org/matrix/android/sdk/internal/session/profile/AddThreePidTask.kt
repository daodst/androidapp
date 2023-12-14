

package org.matrix.android.sdk.internal.session.profile

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.internal.di.SessionDatabase
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task
import org.matrix.android.sdk.internal.util.awaitTransaction
import java.util.UUID
import javax.inject.Inject

internal abstract class AddThreePidTask : Task<AddThreePidTask.Params, Unit> {
    data class Params(
            val threePid: ThreePid
    )
}

internal class DefaultAddThreePidTask @Inject constructor(
        private val profileAPI: ProfileAPI,
        @SessionDatabase private val monarchy: Monarchy,
        private val pendingThreePidMapper: PendingThreePidMapper,
        private val globalErrorReceiver: GlobalErrorReceiver) : AddThreePidTask() {

    override suspend fun execute(params: Params) {
        when (params.threePid) {
            is ThreePid.Email  -> addEmail(params.threePid)
            is ThreePid.Msisdn -> addMsisdn(params.threePid)
        }
    }

    private suspend fun addEmail(threePid: ThreePid.Email) {
        val clientSecret = UUID.randomUUID().toString()
        val sendAttempt = 1

        val body = AddEmailBody(
                clientSecret = clientSecret,
                email = threePid.email,
                sendAttempt = sendAttempt
        )

        val result = executeRequest(globalErrorReceiver) {
            profileAPI.addEmail(body)
        }

        
        monarchy.awaitTransaction { realm ->
            PendingThreePid(
                    threePid = threePid,
                    clientSecret = clientSecret,
                    sendAttempt = sendAttempt,
                    sid = result.sid,
                    submitUrl = null
            )
                    .let { pendingThreePidMapper.map(it) }
                    .let { realm.copyToRealm(it) }
        }
    }

    private suspend fun addMsisdn(threePid: ThreePid.Msisdn) {
        val clientSecret = UUID.randomUUID().toString()
        val sendAttempt = 1

        
        val phoneNumber = threePid.msisdn
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        val parsedNumber = phoneNumberUtil.parse(phoneNumber, null)
        val countryCode = parsedNumber.countryCode
        val country = phoneNumberUtil.getRegionCodeForCountryCode(countryCode)

        val body = AddMsisdnBody(
                clientSecret = clientSecret,
                country = country,
                phoneNumber = parsedNumber.nationalNumber.toString(),
                sendAttempt = sendAttempt
        )

        val result = executeRequest(globalErrorReceiver) {
            profileAPI.addMsisdn(body)
        }

        
        monarchy.awaitTransaction { realm ->
            PendingThreePid(
                    threePid = threePid,
                    clientSecret = clientSecret,
                    sendAttempt = sendAttempt,
                    sid = result.sid,
                    submitUrl = result.submitUrl
            )
                    .let { pendingThreePidMapper.map(it) }
                    .let { realm.copyToRealm(it) }
        }
    }
}
