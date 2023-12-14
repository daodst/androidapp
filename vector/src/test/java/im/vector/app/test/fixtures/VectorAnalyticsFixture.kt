

package im.vector.app.test.fixtures

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent
import im.vector.app.features.analytics.itf.VectorAnalyticsScreen

fun aVectorAnalyticsScreen(
        name: String = "a-screen-name",
        properties: Map<String, Any>? = null
) = object : VectorAnalyticsScreen {
    override fun getName() = name
    override fun getProperties() = properties
}

fun aVectorAnalyticsEvent(
        name: String = "an-event-name",
        properties: Map<String, Any>? = null
) = object : VectorAnalyticsEvent {
    override fun getName() = name
    override fun getProperties() = properties
}
