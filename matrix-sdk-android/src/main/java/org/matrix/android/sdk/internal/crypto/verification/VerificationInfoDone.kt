
package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.session.room.model.message.ValidVerificationDone

internal interface VerificationInfoDone : VerificationInfo<ValidVerificationDone> {

    override fun asValidObject(): ValidVerificationDone? {
        val validTransactionId = transactionId?.takeIf { it.isNotEmpty() } ?: return null
        return ValidVerificationDone(validTransactionId)
    }
}
