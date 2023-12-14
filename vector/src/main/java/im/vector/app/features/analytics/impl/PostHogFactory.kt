

package im.vector.app.features.analytics.impl

import android.content.Context
import com.posthog.android.PostHog
import im.vector.app.BuildConfig
import im.vector.app.config.analyticsConfig
import javax.inject.Inject

class PostHogFactory @Inject constructor(private val context: Context) {

    fun createPosthog(): PostHog {
        return PostHog.Builder(context, analyticsConfig.postHogApiKey, analyticsConfig.postHogHost)
                
                
                
                
                
                
                
                
                
                
                
                .collectDeviceId(false)
                .logLevel(getLogLevel())
                .build()
    }

    private fun getLogLevel(): PostHog.LogLevel {
        return if (BuildConfig.DEBUG) {
            PostHog.LogLevel.DEBUG
        } else {
            PostHog.LogLevel.INFO
        }
    }
}
