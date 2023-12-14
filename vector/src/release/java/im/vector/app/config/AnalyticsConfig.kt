

package im.vector.app.config

import im.vector.app.BuildConfig
import im.vector.app.features.analytics.AnalyticsConfig

val analyticsConfig: AnalyticsConfig = object : AnalyticsConfig {
    override val isEnabled = BuildConfig.APPLICATION_ID == "im.vector.app"
    override val postHogHost = "https://posthog.hss.element.io"
    override val postHogApiKey = "phc_Jzsm6DTm6V2705zeU5dcNvQDlonOR68XvX2sh1sEOHO"
    override val policyLink = "https://element.io/cookie-policy"
}
