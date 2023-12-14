

package org.matrix.android.sdk.api.session.room.failure

import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.room.alias.RoomAliasError
import org.matrix.android.sdk.api.session.room.name.RoomNameError

sealed class CreateRoomFailure : Failure.FeatureFailure() {
    data class CreatedWithTimeout(val roomID: String) : CreateRoomFailure()
    data class CreatedWithFederationFailure(val matrixError: MatrixError) : CreateRoomFailure()
    data class AliasError(val aliasError: RoomAliasError) : CreateRoomFailure()
    data class NameError(val nameError: RoomNameError) : CreateRoomFailure()
}
