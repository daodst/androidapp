

package im.vector.app.features.location

import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import com.airbnb.mvrx.MavericksState
import im.vector.app.R
import org.matrix.android.sdk.api.extensions.orTrue
import org.matrix.android.sdk.api.util.MatrixItem

enum class LocationSharingMode(@StringRes val titleRes: Int) {
    STATIC_SHARING(R.string.location_activity_title_static_sharing),
    PREVIEW(R.string.location_activity_title_preview)
}

data class LocationSharingViewState(
        val roomId: String,
        val mode: LocationSharingMode,
        val userItem: MatrixItem.UserItem? = null,
        val areTargetAndUserLocationEqual: Boolean? = null,
        val lastKnownUserLocation: LocationData? = null,
        val locationTargetDrawable: Drawable? = null
) : MavericksState {

    constructor(locationSharingArgs: LocationSharingArgs) : this(
            roomId = locationSharingArgs.roomId,
            mode = locationSharingArgs.mode,
    )
}

fun LocationSharingViewState.toMapState() = MapState(
        zoomOnlyOnce = true,
        userLocationData = lastKnownUserLocation,
        pinId = DEFAULT_PIN_ID,
        pinDrawable = null,
        
        showPin = areTargetAndUserLocationEqual.orTrue().not()
)
