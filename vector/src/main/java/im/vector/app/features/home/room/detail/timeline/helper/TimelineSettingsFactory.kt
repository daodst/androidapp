

package im.vector.app.features.home.room.detail.timeline.helper

import im.vector.app.core.resources.UserPreferencesProvider
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import javax.inject.Inject

class TimelineSettingsFactory @Inject constructor(private val userPreferencesProvider: UserPreferencesProvider) {

    fun create(rootThreadEventId: String?): TimelineSettings {
        return TimelineSettings(
                initialSize = 30,
                buildReadReceipts = userPreferencesProvider.shouldShowReadReceipts(),
                rootThreadEventId = rootThreadEventId
        )
    }
}
