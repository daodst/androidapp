

package im.vector.app.config

import im.vector.app.BuildConfig
import im.vector.app.features.analytics.AnalyticsConfig

val analyticsConfig: AnalyticsConfig = object : AnalyticsConfig {
    override val isEnabled = BuildConfig.APPLICATION_ID == "im.vector.app.debug"
    override val postHogHost = "https://posthog.element.dev"
    override val postHogApiKey = "phc_VtA1L35nw3aeAtHIx1ayrGdzGkss7k1xINeXcoIQzXN"
    override val policyLink = "https://element.io/cookie-policy"
}
