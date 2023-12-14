

package im.vector.app.features.location.domain.usecase

import com.mapbox.mapboxsdk.geometry.LatLng
import im.vector.app.features.location.LocationData
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject


private const val SAME_LOCATION_THRESHOLD_IN_METERS = 5


class CompareLocationsUseCase @Inject constructor(
        private val session: Session
) {

    
    suspend fun execute(location1: LocationData, location2: LocationData): Boolean =
            withContext(session.coroutineDispatchers.io) {
                val loc1 = LatLng(location1.latitude, location1.longitude)
                val loc2 = LatLng(location2.latitude, location2.longitude)
                val distance = loc1.distanceTo(loc2)
                distance <= SAME_LOCATION_THRESHOLD_IN_METERS
            }
}
