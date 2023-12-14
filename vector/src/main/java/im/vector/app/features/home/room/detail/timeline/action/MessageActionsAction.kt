

package im.vector.app.features.home.room.detail.timeline.action

import im.vector.app.core.platform.VectorViewModelAction

sealed class MessageActionsAction : VectorViewModelAction {
    object ToggleReportMenu : MessageActionsAction()
}
