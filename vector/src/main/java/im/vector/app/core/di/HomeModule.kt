

package im.vector.app.core.di

import android.os.Handler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import im.vector.app.features.home.room.detail.timeline.TimelineEventControllerHandler
import im.vector.app.features.home.room.detail.timeline.helper.TimelineAsyncHelper

@Module
@InstallIn(ActivityComponent::class)
object HomeModule {
    @Provides
    @TimelineEventControllerHandler
    fun providesTimelineBackgroundHandler(): Handler {
        return TimelineAsyncHelper.getBackgroundHandler()
    }
}
