
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.SendToDeviceObject
import org.matrix.android.sdk.api.session.crypto.verification.CancelCode
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoCancel


@JsonClass(generateAdapter = true)
internal data class KeyVerificationCancel(
        
        @Json(name = "transaction_id")
        override val transactionId: String? = null,

        
        override val code: String? = null,

        
        override val reason: String? = null
) : SendToDeviceObject, VerificationInfoCancel {

    companion object {
        fun create(tid: String, cancelCode: CancelCode): KeyVerificationCancel {
            return KeyVerificationCancel(
                    tid,
                    cancelCode.value,
                    cancelCode.humanReadable
            )
        }
    }

    override fun toSendToDeviceObject() = this
}
