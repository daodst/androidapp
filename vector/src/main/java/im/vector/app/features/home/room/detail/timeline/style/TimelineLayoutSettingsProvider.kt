

package im.vector.app.features.home.room.detail.timeline.style

import im.vector.app.features.settings.VectorPreferences
import javax.inject.Inject

class TimelineLayoutSettingsProvider @Inject constructor(private val vectorPreferences: VectorPreferences) {

    fun getLayoutSettings(): TimelineLayoutSettings {
        return if (vectorPreferences.useMessageBubblesLayout()) {
            TimelineLayoutSettings.BUBBLE
        } else {
            TimelineLayoutSettings.MODERN
        }
    }
}
