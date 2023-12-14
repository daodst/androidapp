

package im.vector.app.features.location

import im.vector.app.core.platform.VectorViewModelAction

sealed class LocationSharingAction : VectorViewModelAction {
    object CurrentUserLocationSharing : LocationSharingAction()
    data class PinnedLocationSharing(val locationData: LocationData?) : LocationSharingAction()
    data class LocationTargetChange(val locationData: LocationData) : LocationSharingAction()
    object ZoomToUserLocation : LocationSharingAction()
    data class StartLiveLocationSharing(val durationMillis: Long) : LocationSharingAction()
}
