
package org.matrix.android.sdk.internal.session.pushers

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.di.SerializeNulls
import java.security.InvalidParameterException


@JsonClass(generateAdapter = true)
internal data class JsonPusher(
        
        @Json(name = "pushkey")
        val pushKey: String,

        
        @SerializeNulls
        @Json(name = "kind")
        val kind: String?,

        
        @Json(name = "app_id")
        val appId: String,

        
        @Json(name = "app_display_name")
        val appDisplayName: String? = null,

        
        @Json(name = "device_display_name")
        val deviceDisplayName: String? = null,

        
        @Json(name = "profile_tag")
        val profileTag: String? = null,

        
        @Json(name = "lang")
        val lang: String? = null,

        
        @Json(name = "data")
        val data: JsonPusherData? = null,

        
        @Json(name = "append")
        val append: Boolean? = false
) {
    init {
        
        if (pushKey.length > 512) throw InvalidParameterException("pushkey should not exceed 512 chars")
        if (appId.length > 64) throw InvalidParameterException("appId should not exceed 64 chars")
        data?.url?.let { url -> if ("/_matrix/push/v1/notify" !in url) throw InvalidParameterException("url should contain '/_matrix/push/v1/notify'") }
    }
}
