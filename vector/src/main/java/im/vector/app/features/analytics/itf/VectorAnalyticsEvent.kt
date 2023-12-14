

package im.vector.app.features.analytics.itf

interface VectorAnalyticsEvent {
    fun getName(): String
    fun getProperties(): Map<String, Any?>?
}
