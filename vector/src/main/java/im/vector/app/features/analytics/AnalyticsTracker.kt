

package im.vector.app.features.analytics

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent
import im.vector.app.features.analytics.itf.VectorAnalyticsScreen
import im.vector.app.features.analytics.plan.UserProperties

interface AnalyticsTracker {
    
    fun capture(event: VectorAnalyticsEvent)

    
    fun screen(screen: VectorAnalyticsScreen)

    
    fun updateUserProperties(userProperties: UserProperties)
}
