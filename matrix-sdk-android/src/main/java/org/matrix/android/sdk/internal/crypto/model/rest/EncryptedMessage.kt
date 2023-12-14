

package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.session.crypto.model.SendToDeviceObject

@JsonClass(generateAdapter = true)
internal data class EncryptedMessage(
        @Json(name = "algorithm")
        val algorithm: String? = null,

        @Json(name = "sender_key")
        val senderKey: String? = null,

        @Json(name = "ciphertext")
        val cipherText: Map<String, Any>? = null
) : SendToDeviceObject
