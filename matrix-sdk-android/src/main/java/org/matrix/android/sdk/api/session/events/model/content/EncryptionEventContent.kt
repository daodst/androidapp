
package org.matrix.android.sdk.api.session.events.model.content

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class EncryptionEventContent(
        
        @Json(name = "algorithm")
        val algorithm: String?,

        
        @Json(name = "rotation_period_ms")
        val rotationPeriodMs: Long? = null,

        
        @Json(name = "rotation_period_msgs")
        val rotationPeriodMsgs: Long? = null
)
