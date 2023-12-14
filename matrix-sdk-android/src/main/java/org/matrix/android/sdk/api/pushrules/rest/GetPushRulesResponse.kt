
package org.matrix.android.sdk.api.pushrules.rest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class GetPushRulesResponse(
        
        @Json(name = "global")
        val global: RuleSet,

        
        @Json(name = "device")
        val device: RuleSet? = null
)
