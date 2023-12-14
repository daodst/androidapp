

package org.matrix.android.sdk.api.session.room.model.thirdparty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThirdPartyProtocol(
        
        @Json(name = "user_fields")
        val userFields: List<String>? = null,

        
        @Json(name = "location_fields")
        val locationFields: List<String>? = null,

        
        @Json(name = "icon")
        val icon: String? = null,

        
        @Json(name = "field_types")
        val fieldTypes: Map<String, FieldType>? = null,

        
        @Json(name = "instances")
        val instances: List<ThirdPartyProtocolInstance>? = null
)
