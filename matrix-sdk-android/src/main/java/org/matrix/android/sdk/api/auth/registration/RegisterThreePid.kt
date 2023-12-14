

package org.matrix.android.sdk.api.auth.registration

sealed class RegisterThreePid {
    data class Email(val email: String) : RegisterThreePid()
    data class Msisdn(val msisdn: String, val countryCode: String) : RegisterThreePid()
}
