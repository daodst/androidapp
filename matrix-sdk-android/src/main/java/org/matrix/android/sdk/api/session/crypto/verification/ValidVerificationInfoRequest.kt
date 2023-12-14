

package org.matrix.android.sdk.api.session.crypto.verification

data class ValidVerificationInfoRequest(
        val transactionId: String,
        val fromDevice: String,
        val methods: List<String>,
        val timestamp: Long?
)
