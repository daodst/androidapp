

package org.matrix.android.sdk.api.session.room.model.thirdparty

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThirdPartyProtocolInstance(
        
        @Json(name = "desc")
        val desc: String? = null,

        
        @Json(name = "icon")
        val icon: String? = null,

        
        @Json(name = "fields")
        val fields: Map<String, Any>? = null,

        
        @Json(name = "network_id")
        val networkId: String? = null,

        
        @Json(name = "instance_id")
        val instanceId: String? = null,

        
        @Json(name = "bot_user_id")
        val botUserId: String? = null
)
