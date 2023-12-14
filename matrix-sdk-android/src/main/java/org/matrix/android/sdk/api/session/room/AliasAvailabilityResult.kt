

package org.matrix.android.sdk.api.session.room

import org.matrix.android.sdk.api.session.room.alias.RoomAliasError

sealed class AliasAvailabilityResult {
    object Available : AliasAvailabilityResult()
    data class NotAvailable(val roomAliasError: RoomAliasError) : AliasAvailabilityResult()
}
