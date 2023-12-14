

package org.matrix.android.sdk.api.session.room.name

sealed class RoomNameError : Throwable() {
    object NameNotNull : RoomNameError()
}
