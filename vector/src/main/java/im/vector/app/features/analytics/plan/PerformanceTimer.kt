

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class PerformanceTimer(
        
        val context: String? = null,
        
        val itemCount: Int? = null,
        
        val name: Name,
        
        val timeMs: Int,
) : VectorAnalyticsEvent {

    enum class Name {
        
        InitialSyncParsing,

        
        InitialSyncRequest,

        
        NotificationsOpenEvent,

        
        StartupIncrementalSync,

        
        StartupInitialSync,

        
        StartupLaunchScreen,

        
        StartupStorePreload,

        
        StartupStoreReady,
    }

    override fun getName() = "PerformanceTimer"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            context?.let { put("context", it) }
            itemCount?.let { put("itemCount", it) }
            put("name", name.name)
            put("timeMs", timeMs)
        }.takeIf { it.isNotEmpty() }
    }
}
