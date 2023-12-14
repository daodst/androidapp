

package org.matrix.android.sdk.api.auth.data

object LoginFlowTypes {
    const val PASSWORD = "m.login.password"
    const val OAUTH2 = "m.login.oauth2"
    const val EMAIL_CODE = "m.login.email.code"
    const val EMAIL_URL = "m.login.email.url"
    const val EMAIL_IDENTITY = "m.login.email.identity"
    const val MSISDN = "m.login.msisdn"
    const val RECAPTCHA = "m.login.recaptcha"
    const val DUMMY = "m.login.dummy"
    const val TERMS = "m.login.terms"
    const val TOKEN = "m.login.token"
    const val SSO = "m.login.sso"
    
    const val BLOCKCHAIN = "com.xs.cosmos_sign_auth"
    
    const val JWT = "com.xs.jwt_auth"
}
