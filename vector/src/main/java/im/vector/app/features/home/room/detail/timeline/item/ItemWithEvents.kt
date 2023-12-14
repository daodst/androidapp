

package im.vector.app.features.home.room.detail.timeline.item

interface ItemWithEvents {
    
    fun getEventIds(): List<String>

    fun canAppendReadMarker(): Boolean = true

    fun isVisible(): Boolean = true

    
    fun isCacheable(): Boolean = true
}
