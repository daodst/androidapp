

package org.matrix.android.sdk.api.session.thirdparty.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict

@JsonClass(generateAdapter = true)
data class ThirdPartyUser(
        
        @Json(name = "userid") val userId: String,
        
        @Json(name = "protocol") val protocol: String,
        
        @Json(name = "fields") val fields: JsonDict
)
