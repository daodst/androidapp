

package org.matrix.android.sdk.api.failure

import org.matrix.android.sdk.api.network.ssl.Fingerprint

sealed class GlobalError {
    data class InvalidToken(val softLogout: Boolean) : GlobalError()
    data class ConsentNotGivenError(val consentUri: String) : GlobalError()
    data class CertificateError(val fingerprint: Fingerprint) : GlobalError()

    
    data class InitialSyncRequest(val reason: InitialSyncRequestReason) : GlobalError()
    object ExpiredAccount : GlobalError()
}
