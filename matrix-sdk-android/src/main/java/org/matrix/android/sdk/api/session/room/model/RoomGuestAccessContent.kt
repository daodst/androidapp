

package org.matrix.android.sdk.api.session.room.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import timber.log.Timber


@JsonClass(generateAdapter = true)
data class RoomGuestAccessContent(
        
        @Json(name = "guest_access") val _guestAccess: String? = null
) {
    val guestAccess: GuestAccess? = when (_guestAccess) {
        "can_join"  -> GuestAccess.CanJoin
        "forbidden" -> GuestAccess.Forbidden
        else        -> {
            Timber.w("Invalid value for GuestAccess: `$_guestAccess`")
            null
        }
    }
}

@JsonClass(generateAdapter = false)
enum class GuestAccess(val value: String) {
    @Json(name = "can_join") CanJoin("can_join"),
    @Json(name = "forbidden") Forbidden("forbidden")
}
