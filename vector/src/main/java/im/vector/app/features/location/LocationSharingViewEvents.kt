

package im.vector.app.features.location

import im.vector.app.core.platform.VectorViewEvents

sealed class LocationSharingViewEvents : VectorViewEvents {
    object Close : LocationSharingViewEvents()
    object LocationNotAvailableError : LocationSharingViewEvents()
    data class ZoomToUserLocation(val userLocation: LocationData) : LocationSharingViewEvents()
    data class StartLiveLocationService(val sessionId: String, val roomId: String, val durationMillis: Long) : LocationSharingViewEvents()
}
