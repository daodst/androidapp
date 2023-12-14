

package org.matrix.android.sdk.api.session.room.model.thirdparty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FieldType(
        
        @Json(name = "regexp")
        val regexp: String? = null,

        
        @Json(name = "placeholder")
        val placeholder: String? = null
)
