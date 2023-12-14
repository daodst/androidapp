

package im.vector.app.features.location

import android.graphics.drawable.Drawable
import androidx.annotation.Px

data class MapState(
        val zoomOnlyOnce: Boolean,
        val userLocationData: LocationData? = null,
        val pinId: String,
        val pinDrawable: Drawable? = null,
        val showPin: Boolean = true,
        @Px val logoMarginBottom: Int = 0
)
