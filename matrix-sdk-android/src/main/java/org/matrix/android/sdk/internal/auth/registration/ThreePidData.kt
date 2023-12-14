

package org.matrix.android.sdk.internal.auth.registration

import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.registration.RegisterThreePid


@JsonClass(generateAdapter = true)
internal data class ThreePidData(
        val email: String,
        val msisdn: String,
        val country: String,
        val addThreePidRegistrationResponse: AddThreePidRegistrationResponse,
        val registrationParams: RegistrationParams
) {
    val threePid: RegisterThreePid
        get() {
            return if (email.isNotBlank()) {
                RegisterThreePid.Email(email)
            } else {
                RegisterThreePid.Msisdn(msisdn, country)
            }
        }

    companion object {
        fun from(threePid: RegisterThreePid,
                 addThreePidRegistrationResponse: AddThreePidRegistrationResponse,
                 registrationParams: RegistrationParams): ThreePidData {
            return when (threePid) {
                is RegisterThreePid.Email  ->
                    ThreePidData(threePid.email, "", "", addThreePidRegistrationResponse, registrationParams)
                is RegisterThreePid.Msisdn ->
                    ThreePidData("", threePid.msisdn, threePid.countryCode, addThreePidRegistrationResponse, registrationParams)
            }
        }
    }
}
