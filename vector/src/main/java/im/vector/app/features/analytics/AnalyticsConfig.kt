

package im.vector.app.features.analytics

interface AnalyticsConfig {
    val isEnabled: Boolean
    val postHogHost: String
    val postHogApiKey: String
    val policyLink: String
}
