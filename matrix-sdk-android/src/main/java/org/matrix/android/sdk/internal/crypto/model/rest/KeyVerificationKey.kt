
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.SendToDeviceObject
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoKey
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoKeyFactory


@JsonClass(generateAdapter = true)
internal data class KeyVerificationKey(
        
        @Json(name = "transaction_id") override val transactionId: String? = null,

        
        @Json(name = "key") override val key: String? = null

) : SendToDeviceObject, VerificationInfoKey {

    companion object : VerificationInfoKeyFactory {
        override fun create(tid: String, pubKey: String): KeyVerificationKey {
            return KeyVerificationKey(tid, pubKey)
        }
    }

    override fun toSendToDeviceObject() = this
}
