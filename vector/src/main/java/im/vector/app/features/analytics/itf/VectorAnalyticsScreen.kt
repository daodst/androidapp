

package im.vector.app.features.analytics.itf

interface VectorAnalyticsScreen {
    fun getName(): String
    fun getProperties(): Map<String, Any>?
}
