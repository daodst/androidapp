

package org.matrix.android.sdk.api.session.sync.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupSyncProfile(
        
        @Json(name = "name") val name: String? = null,

        
        @Json(name = "avatar_url") val avatarUrl: String? = null
)
