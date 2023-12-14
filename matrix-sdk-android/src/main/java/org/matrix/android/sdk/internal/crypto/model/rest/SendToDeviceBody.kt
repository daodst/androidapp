

package org.matrix.android.sdk.internal.crypto.model.rest

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SendToDeviceBody(
        
        val messages: Map<String, Map<String, Any>>?
)
