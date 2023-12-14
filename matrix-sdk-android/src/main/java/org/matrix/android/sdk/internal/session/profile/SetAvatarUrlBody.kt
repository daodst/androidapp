
package org.matrix.android.sdk.internal.session.profile

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SetAvatarUrlBody(
        
        @Json(name = "avatar_url")
        val avatarUrl: String
)
