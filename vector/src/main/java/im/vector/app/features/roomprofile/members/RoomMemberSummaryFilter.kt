

package im.vector.app.features.roomprofile.members

import androidx.core.util.Predicate
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import javax.inject.Inject

class RoomMemberSummaryFilter @Inject constructor() : Predicate<RoomMemberSummary> {
    var filter: String = ""

    override fun test(roomMemberSummary: RoomMemberSummary): Boolean {
        if (filter.isEmpty()) {
            
            return true
        }
        
        return filter.split(" ").all {
            roomMemberSummary.displayName?.contains(it, ignoreCase = true).orFalse() ||
                    
                    roomMemberSummary.userId.contains(it, ignoreCase = true)
        }
    }
}
