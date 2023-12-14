

package im.vector.app.features.analytics.accountdata

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AnalyticsAccountDataContent(
        
        
        @Json(name = "id")
        val id: String? = null,
        
        
        @Json(name = "pseudonymousAnalyticsOptIn")
        val pseudonymousAnalyticsOptIn: Boolean? = null,
        
        @Json(name = "showPseudonymousAnalyticsPrompt")
        val showPseudonymousAnalyticsPrompt: Boolean? = null
)
