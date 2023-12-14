

package org.matrix.android.sdk.api.failure

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.uia.InteractiveAuthenticationFlow
import org.matrix.android.sdk.api.util.JsonDict


@JsonClass(generateAdapter = true)
data class MatrixError(
        
        @Json(name = "errcode") val code: String,
        
        @Json(name = "error") val message: String,

        
        @Json(name = "consent_uri") val consentUri: String? = null,
        
        @Json(name = "limit_type") val limitType: String? = null,
        @Json(name = "admin_contact") val adminUri: String? = null,
        
        @Json(name = "retry_after_ms") val retryAfterMillis: Long? = null,
        
        @Json(name = "soft_logout") val isSoftLogout: Boolean? = null,
        
        
        @Json(name = "lookup_pepper") val newLookupPepper: String? = null,

        
        @Json(name = "session")
        val session: String? = null,
        @Json(name = "completed")
        val completedStages: List<String>? = null,
        @Json(name = "flows")
        val flows: List<InteractiveAuthenticationFlow>? = null,
        @Json(name = "params")
        val params: JsonDict? = null,
        @Json(name = "maxUploadFileSize")
        var maxUploadFileSize: Long? = null,

) {

    companion object {
        
        const val M_FORBIDDEN = "M_FORBIDDEN"

        
        const val M_UNKNOWN = "M_UNKNOWN"

        
        const val M_UNKNOWN_TOKEN = "M_UNKNOWN_TOKEN"

        
        const val M_MISSING_TOKEN = "M_MISSING_TOKEN"

        
        const val M_BAD_JSON = "M_BAD_JSON"

        
        const val M_NOT_JSON = "M_NOT_JSON"

        
        const val M_NOT_FOUND = "M_NOT_FOUND"

        
        const val M_LIMIT_EXCEEDED = "M_LIMIT_EXCEEDED"

        

        
        const val M_USER_IN_USE = "M_USER_IN_USE"

        
        const val M_ROOM_IN_USE = "M_ROOM_IN_USE"

        
        const val M_BAD_PAGINATION = "M_BAD_PAGINATION"

        
        const val M_UNAUTHORIZED = "M_UNAUTHORIZED"

        
        const val M_OLD_VERSION = "M_OLD_VERSION"

        
        const val M_UNRECOGNIZED = "M_UNRECOGNIZED"

        
        const val M_LOGIN_EMAIL_URL_NOT_YET = "M_LOGIN_EMAIL_URL_NOT_YET"

        
        const val M_THREEPID_AUTH_FAILED = "M_THREEPID_AUTH_FAILED"

        
        const val M_THREEPID_NOT_FOUND = "M_THREEPID_NOT_FOUND"

        
        const val M_THREEPID_IN_USE = "M_THREEPID_IN_USE"

        
        const val M_SERVER_NOT_TRUSTED = "M_SERVER_NOT_TRUSTED"

        
        const val M_TOO_LARGE = "M_TOO_LARGE"

        
        const val M_CONSENT_NOT_GIVEN = "M_CONSENT_NOT_GIVEN"

        
        const val M_RESOURCE_LIMIT_EXCEEDED = "M_RESOURCE_LIMIT_EXCEEDED"

        
        const val M_USER_DEACTIVATED = "M_USER_DEACTIVATED"

        
        const val M_INVALID_USERNAME = "M_INVALID_USERNAME"

        
        const val M_INVALID_ROOM_STATE = "M_INVALID_ROOM_STATE"

        
        const val M_THREEPID_DENIED = "M_THREEPID_DENIED"

        
        const val M_UNSUPPORTED_ROOM_VERSION = "M_UNSUPPORTED_ROOM_VERSION"

        
        const val M_INCOMPATIBLE_ROOM_VERSION = "M_INCOMPATIBLE_ROOM_VERSION"

        
        const val M_BAD_STATE = "M_BAD_STATE"

        
        const val M_GUEST_ACCESS_FORBIDDEN = "M_GUEST_ACCESS_FORBIDDEN"

        
        const val M_CAPTCHA_NEEDED = "M_CAPTCHA_NEEDED"

        
        const val M_CAPTCHA_INVALID = "M_CAPTCHA_INVALID"

        
        const val M_MISSING_PARAM = "M_MISSING_PARAM"

        
        const val M_INVALID_PARAM = "M_INVALID_PARAM"

        
        const val M_EXCLUSIVE = "M_EXCLUSIVE"

        
        const val M_CANNOT_LEAVE_SERVER_NOTICE_ROOM = "M_CANNOT_LEAVE_SERVER_NOTICE_ROOM"

        
        const val M_WRONG_ROOM_KEYS_VERSION = "M_WRONG_ROOM_KEYS_VERSION"

        
        const val M_WEAK_PASSWORD = "M_WEAK_PASSWORD"

        
        const val M_PASSWORD_TOO_SHORT = "M_PASSWORD_TOO_SHORT"

        
        const val M_PASSWORD_NO_DIGIT = "M_PASSWORD_NO_DIGIT"

        
        const val M_PASSWORD_NO_UPPERCASE = "M_PASSWORD_NO_UPPERCASE"

        
        const val M_PASSWORD_NO_LOWERCASE = "M_PASSWORD_NO_LOWERCASE"

        
        const val M_PASSWORD_NO_SYMBOL = "M_PASSWORD_NO_SYMBOL"

        
        const val M_PASSWORD_IN_DICTIONARY = "M_PASSWORD_IN_DICTIONARY"

        const val M_TERMS_NOT_SIGNED = "M_TERMS_NOT_SIGNED"

        
        const val M_INVALID_PEPPER = "M_INVALID_PEPPER"

        
        const val LIMIT_TYPE_MAU = "monthly_active_user"

        
        const val ORG_MATRIX_EXPIRED_ACCOUNT = "ORG_MATRIX_EXPIRED_ACCOUNT"
    }
}
