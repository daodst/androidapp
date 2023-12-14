

package im.vector.app.fdroid.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import im.vector.app.core.services.GuardServiceStarter
import im.vector.app.features.settings.VectorPreferences
import timber.log.Timber
import javax.inject.Inject

class FDroidGuardServiceStarter @Inject constructor(
        private val preferences: VectorPreferences,
        private val appContext: Context
) : GuardServiceStarter {

    override fun start() {
        if (preferences.isBackgroundSyncEnabled()) {
            try {
                Timber.i("## Sync: starting GuardService")
                val intent = Intent(appContext, GuardService::class.java)
                ContextCompat.startForegroundService(appContext, intent)
            } catch (ex: Throwable) {
                Timber.e("## Sync: ERROR starting GuardService")
            }
        }
    }

    override fun stop() {
        val intent = Intent(appContext, GuardService::class.java)
        appContext.stopService(intent)
    }
}
