

package org.matrix.android.sdk.api.session.crypto.verification

data class ValidVerificationInfoReady(
        val transactionId: String,
        val fromDevice: String,
        val methods: List<String>
)
