

package im.vector.app.features.widgets.permissions

import im.vector.app.core.platform.VectorViewEvents

sealed class RoomWidgetPermissionViewEvents : VectorViewEvents {
    object Close : RoomWidgetPermissionViewEvents()
}
