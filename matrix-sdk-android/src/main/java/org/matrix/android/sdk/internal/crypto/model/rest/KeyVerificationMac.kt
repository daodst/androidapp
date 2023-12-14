
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.SendToDeviceObject
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoMac
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoMacFactory


@JsonClass(generateAdapter = true)
internal data class KeyVerificationMac(
        @Json(name = "transaction_id") override val transactionId: String? = null,
        @Json(name = "mac") override val mac: Map<String, String>? = null,
        @Json(name = "keys") override val keys: String? = null

) : SendToDeviceObject, VerificationInfoMac {

    override fun toSendToDeviceObject(): SendToDeviceObject? = this

    companion object : VerificationInfoMacFactory {
        override fun create(tid: String, mac: Map<String, String>, keys: String): VerificationInfoMac {
            return KeyVerificationMac(tid, mac, keys)
        }
    }
}
