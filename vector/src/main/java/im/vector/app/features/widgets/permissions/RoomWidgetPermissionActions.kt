

package im.vector.app.features.widgets.permissions

import im.vector.app.core.platform.VectorViewModelAction

sealed class RoomWidgetPermissionActions : VectorViewModelAction {
    object AllowWidget : RoomWidgetPermissionActions()
    object BlockWidget : RoomWidgetPermissionActions()
    object DoClose : RoomWidgetPermissionActions()
}
