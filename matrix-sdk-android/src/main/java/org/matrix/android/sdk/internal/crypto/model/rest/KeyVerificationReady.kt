
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.SendToDeviceObject
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoReady


@JsonClass(generateAdapter = true)
internal data class KeyVerificationReady(
        @Json(name = "from_device") override val fromDevice: String?,
        @Json(name = "methods") override val methods: List<String>?,
        @Json(name = "transaction_id") override val transactionId: String? = null
) : SendToDeviceObject, VerificationInfoReady {

    override fun toSendToDeviceObject() = this
}
