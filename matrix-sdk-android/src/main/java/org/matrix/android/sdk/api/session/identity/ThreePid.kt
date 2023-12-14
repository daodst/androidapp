

package org.matrix.android.sdk.api.session.identity

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.matrix.android.sdk.internal.session.profile.ThirdPartyIdentifier

sealed class ThreePid(open val value: String) {
    data class Email(val email: String) : ThreePid(email)
    data class Msisdn(val msisdn: String) : ThreePid(msisdn)
}

internal fun ThreePid.toMedium(): String {
    return when (this) {
        is ThreePid.Email  -> ThirdPartyIdentifier.MEDIUM_EMAIL
        is ThreePid.Msisdn -> ThirdPartyIdentifier.MEDIUM_MSISDN
    }
}

@Throws(NumberParseException::class)
internal fun ThreePid.Msisdn.getCountryCode(): String {
    return with(PhoneNumberUtil.getInstance()) {
        getRegionCodeForCountryCode(parse("+$msisdn", null).countryCode)
    }
}
