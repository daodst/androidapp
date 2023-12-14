

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class CallEnded(
        
        val durationMs: Int,
        
        val isVideo: Boolean,
        
        val numParticipants: Int,
        
        val placed: Boolean,
) : VectorAnalyticsEvent {

    override fun getName() = "CallEnded"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            put("durationMs", durationMs)
            put("isVideo", isVideo)
            put("numParticipants", numParticipants)
            put("placed", placed)
        }.takeIf { it.isNotEmpty() }
    }
}
