

package org.matrix.android.sdk.internal.session.group.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class GroupSummaryUser(

        
        @Json(name = "membership") val membership: String? = null,

        
        @Json(name = "is_publicised") val isPublicised: Boolean? = null
)
