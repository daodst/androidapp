

package org.matrix.android.sdk.api.session.room.model.call

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.extensions.orFalse

@JsonClass(generateAdapter = true)
data class CallCapabilities(
        
        @Json(name = "m.call.transferee") val transferee: Boolean? = null
)

fun CallCapabilities?.supportCallTransfer() = this?.transferee.orFalse()
