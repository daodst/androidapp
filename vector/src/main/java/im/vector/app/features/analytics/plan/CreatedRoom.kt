

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class CreatedRoom(
        
        val isDM: Boolean,
) : VectorAnalyticsEvent {

    override fun getName() = "CreatedRoom"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            put("isDM", isDM)
        }.takeIf { it.isNotEmpty() }
    }
}
