

package org.matrix.android.sdk.internal.auth.registration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.registration.RegisterThreePid


@JsonClass(generateAdapter = true)
internal data class AddThreePidRegistrationParams(
        
        @Json(name = "client_secret")
        val clientSecret: String,

        
        @Json(name = "send_attempt")
        val sendAttempt: Int,

        
        @Json(name = "next_link")
        val nextLink: String? = null,

        
        @Json(name = "id_server")
        val idServer: String? = null,

        

        
        @Json(name = "email")
        val email: String? = null,

        

        
        @Json(name = "country")
        val countryCode: String? = null,

        
        @Json(name = "phone_number")
        val msisdn: String? = null
) {
    companion object {
        fun from(params: RegisterAddThreePidTask.Params): AddThreePidRegistrationParams {
            return when (params.threePid) {
                is RegisterThreePid.Email  -> AddThreePidRegistrationParams(
                        email = params.threePid.email,
                        clientSecret = params.clientSecret,
                        sendAttempt = params.sendAttempt
                )
                is RegisterThreePid.Msisdn -> AddThreePidRegistrationParams(
                        msisdn = params.threePid.msisdn,
                        countryCode = params.threePid.countryCode,
                        clientSecret = params.clientSecret,
                        sendAttempt = params.sendAttempt
                )
            }
        }
    }
}
