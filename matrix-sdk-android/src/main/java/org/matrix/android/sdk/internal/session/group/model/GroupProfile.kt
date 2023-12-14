

package org.matrix.android.sdk.internal.session.group.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class GroupProfile(

        @Json(name = "short_description") val shortDescription: String? = null,

        
        @Json(name = "is_public") val isPublic: Boolean? = null,

        
        @Json(name = "avatar_url") val avatarUrl: String? = null,

        
        @Json(name = "name") val name: String? = null,

        
        @Json(name = "long_description") val longDescription: String? = null
)
