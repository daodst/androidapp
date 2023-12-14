

package org.matrix.android.sdk.api.session.room.timeline


data class TimelineSettings(
        
        val initialSize: Int,
        
        val buildReadReceipts: Boolean = true,
        
        val rootThreadEventId: String? = null,
) {

    
    fun isThreadTimeline() = rootThreadEventId != null
}
