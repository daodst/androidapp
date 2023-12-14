

package im.vector.app.features.home.room.detail.timeline.action

import im.vector.app.features.home.room.detail.timeline.helper.LocationPinProvider


data class LocationUiData(
        val locationUrl: String,
        val locationOwnerId: String?,
        val locationPinProvider: LocationPinProvider,
)
