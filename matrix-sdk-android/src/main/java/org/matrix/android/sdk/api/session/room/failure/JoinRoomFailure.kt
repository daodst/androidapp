

package org.matrix.android.sdk.api.session.room.failure

import org.matrix.android.sdk.api.failure.Failure

sealed class JoinRoomFailure : Failure.FeatureFailure() {

    object JoinedWithTimeout : JoinRoomFailure()
}
