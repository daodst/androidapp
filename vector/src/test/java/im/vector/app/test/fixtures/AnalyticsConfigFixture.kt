

package im.vector.app.test.fixtures

import im.vector.app.features.analytics.AnalyticsConfig

object AnalyticsConfigFixture {
    fun anAnalyticsConfig(
            isEnabled: Boolean = false,
            postHogHost: String = "http://posthog.url",
            postHogApiKey: String = "api-key",
            policyLink: String = "http://policy.link"
    ) = object : AnalyticsConfig {
        override val isEnabled: Boolean = isEnabled
        override val postHogHost = postHogHost
        override val postHogApiKey = postHogApiKey
        override val policyLink = policyLink
    }
}
