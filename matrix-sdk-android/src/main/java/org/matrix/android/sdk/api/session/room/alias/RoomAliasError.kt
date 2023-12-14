

package org.matrix.android.sdk.api.session.room.alias

sealed class RoomAliasError : Throwable() {
    object AliasIsBlank : RoomAliasError()
    object AliasNotAvailable : RoomAliasError()
    object AliasInvalid : RoomAliasError()
}
