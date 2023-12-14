
package im.vector.app.features.home.room.detail.timeline.action

import im.vector.app.core.platform.VectorSharedActionViewModel
import javax.inject.Inject


class MessageSharedActionViewModel @Inject constructor() : VectorSharedActionViewModel<EventSharedAction>() {
    var pendingAction: EventSharedAction? = null
}
