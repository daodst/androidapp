

package im.vector.app.features.spaces.manage

import androidx.core.util.Predicate
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo

class SpaceChildInfoMatchFilter : Predicate<SpaceChildInfo> {
    var filter: String = ""

    override fun test(spaceChildInfo: SpaceChildInfo): Boolean {
        if (filter.isEmpty()) {
            
            return true
        }
        
        return filter.split(" ").all {
            spaceChildInfo.name?.contains(it, ignoreCase = true).orFalse() ||
                    spaceChildInfo.topic?.contains(it, ignoreCase = true).orFalse()
        }
    }
}
