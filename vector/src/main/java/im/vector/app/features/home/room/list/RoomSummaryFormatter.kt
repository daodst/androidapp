

package im.vector.app.features.home.room.list

object RoomSummaryFormatter {

    
    fun formatUnreadMessagesCounter(count: Int): String {
        return if (count > 999) {
            "${count / 1000}.${count % 1000 / 100}k"
        } else {
            count.toString()
        }
    }
}
