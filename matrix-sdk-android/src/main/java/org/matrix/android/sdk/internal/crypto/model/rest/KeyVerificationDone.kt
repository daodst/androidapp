
package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.SendToDeviceObject
import org.matrix.android.sdk.internal.crypto.verification.VerificationInfoDone


@JsonClass(generateAdapter = true)
internal data class KeyVerificationDone(
        @Json(name = "transaction_id") override val transactionId: String? = null
) : SendToDeviceObject, VerificationInfoDone {

    override fun toSendToDeviceObject() = this
}
