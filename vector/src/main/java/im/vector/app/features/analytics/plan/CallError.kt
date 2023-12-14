

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class CallError(
        
        val isVideo: Boolean,
        
        val numParticipants: Int,
        
        val placed: Boolean,
) : VectorAnalyticsEvent {

    override fun getName() = "CallError"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            put("isVideo", isVideo)
            put("numParticipants", numParticipants)
            put("placed", placed)
        }.takeIf { it.isNotEmpty() }
    }
}
