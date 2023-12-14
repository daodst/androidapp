

package im.vector.app.features.analytics.plan

import im.vector.app.features.analytics.itf.VectorAnalyticsEvent



data class Composer(
        
        val inThread: Boolean,
        
        val isEditing: Boolean,
        
        val isReply: Boolean,
        
        val startsThread: Boolean? = null,
) : VectorAnalyticsEvent {

    override fun getName() = "Composer"

    override fun getProperties(): Map<String, Any>? {
        return mutableMapOf<String, Any>().apply {
            put("inThread", inThread)
            put("isEditing", isEditing)
            put("isReply", isReply)
            startsThread?.let { put("startsThread", it) }
        }.takeIf { it.isNotEmpty() }
    }
}
