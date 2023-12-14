

package org.matrix.android.sdk.internal.crypto.model.rest

import org.matrix.android.sdk.api.session.crypto.verification.VerificationMethod

internal const val VERIFICATION_METHOD_SAS = "m.sas.v1"

internal const val VERIFICATION_METHOD_QR_CODE_SHOW = "m.qr_code.show.v1"
internal const val VERIFICATION_METHOD_QR_CODE_SCAN = "m.qr_code.scan.v1"
internal const val VERIFICATION_METHOD_RECIPROCATE = "m.reciprocate.v1"

internal fun VerificationMethod.toValue(): String {
    return when (this) {
        VerificationMethod.SAS          -> VERIFICATION_METHOD_SAS
        VerificationMethod.QR_CODE_SCAN -> VERIFICATION_METHOD_QR_CODE_SCAN
        VerificationMethod.QR_CODE_SHOW -> VERIFICATION_METHOD_QR_CODE_SHOW
    }
}
