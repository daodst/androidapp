

package im.vector.app.features.settings.locale

import android.content.Context
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

class SystemLocaleProvider @Inject constructor(
        private val context: Context
) {

    
    fun getSystemLocale(): Locale? {
        return try {
            val packageManager = context.packageManager
            val resources = packageManager.getResourcesForApplication("android")
            @Suppress("DEPRECATION")
            resources.configuration.locale
        } catch (e: Exception) {
            Timber.e(e, "## getDeviceLocale() failed")
            null
        }
    }
}
