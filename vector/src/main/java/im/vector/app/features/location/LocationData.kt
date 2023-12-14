

package im.vector.app.features.location

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import kotlinx.parcelize.Parcelize
import org.matrix.android.sdk.api.session.room.model.message.MessageLocationContent

@Parcelize
data class LocationData(
        val latitude: Double,
        val longitude: Double,
        val uncertainty: Double?
) : Parcelable


fun MessageLocationContent.toLocationData(): LocationData? {
    return parseGeo(getBestGeoUri())
}

@VisibleForTesting
fun parseGeo(geo: String): LocationData? {
    val geoParts = geo
            .split(":")
            .takeIf { it.firstOrNull() == "geo" }
            ?.getOrNull(1)
            ?.split(";") ?: return null

    val gpsParts = geoParts.getOrNull(0)?.split(",") ?: return null
    val lat = gpsParts.getOrNull(0)?.toDoubleOrNull() ?: return null
    val lng = gpsParts.getOrNull(1)?.toDoubleOrNull() ?: return null

    val uncertainty = geoParts.getOrNull(1)?.replace("u=", "")?.toDoubleOrNull()

    return LocationData(
            latitude = lat,
            longitude = lng,
            uncertainty = uncertainty
    )
}
