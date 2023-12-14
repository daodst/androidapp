

package org.matrix.android.sdk.api.session.contentscanner

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContentScannerError(
        @Json(name = "info") val info: String? = null,
        @Json(name = "reason") val reason: String? = null
) {
    companion object {
        
        const val REASON_MCS_MEDIA_REQUEST_FAILED = "MCS_MEDIA_REQUEST_FAILED"

        
        const val REASON_MCS_MEDIA_FAILED_TO_DECRYPT = "MCS_MEDIA_FAILED_TO_DECRYPT"

        
        const val REASON_MCS_MEDIA_NOT_CLEAN = "MCS_MEDIA_NOT_CLEAN"

        
        const val REASON_MCS_BAD_DECRYPTION = "MCS_BAD_DECRYPTION"

        
        const val REASON_MCS_MALFORMED_JSON = "MCS_MALFORMED_JSON"
    }
}

class ScanFailure(val error: ContentScannerError, val httpCode: Int, cause: Throwable? = null) : Throwable(cause = cause)

fun ScanFailure.toException() = Exception(this)
fun Throwable.toScanFailure() = this.cause as? ScanFailure
