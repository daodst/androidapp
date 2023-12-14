

package org.matrix.android.sdk.internal.crypto.algorithms

import org.matrix.android.sdk.api.session.events.model.content.RoomKeyWithHeldContent

internal interface IMXWithHeldExtension {
    fun onRoomKeyWithHeldEvent(withHeldInfo: RoomKeyWithHeldContent)
}
