

package org.matrix.android.sdk.internal.auth.registration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes


@JsonClass(generateAdapter = true)
internal data class AuthParams(
        @Json(name = "type")
        val type: String,

        
        @Json(name = "session")
        val session: String?,

        
        @Json(name = "response")
        val captchaResponse: String? = null,

        
        @Json(name = "threepid_creds")
        val threePidCredentials: ThreePidCredentials? = null
) {

    companion object {
        fun createForCaptcha(session: String, captchaResponse: String): AuthParams {
            return AuthParams(
                    type = LoginFlowTypes.RECAPTCHA,
                    session = session,
                    captchaResponse = captchaResponse
            )
        }

        fun createForEmailIdentity(session: String, threePidCredentials: ThreePidCredentials): AuthParams {
            return AuthParams(
                    type = LoginFlowTypes.EMAIL_IDENTITY,
                    session = session,
                    threePidCredentials = threePidCredentials
            )
        }

        
        fun createForMsisdnIdentity(session: String, threePidCredentials: ThreePidCredentials): AuthParams {
            return AuthParams(
                    type = LoginFlowTypes.MSISDN,
                    session = session,
                    threePidCredentials = threePidCredentials
            )
        }

        fun createForResetPassword(clientSecret: String, sid: String): AuthParams {
            return AuthParams(
                    type = LoginFlowTypes.EMAIL_IDENTITY,
                    session = null,
                    threePidCredentials = ThreePidCredentials(
                            clientSecret = clientSecret,
                            sid = sid
                    )
            )
        }
    }
}
