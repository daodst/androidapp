

package im.vector.app.core.pushers

import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.resources.AppNameProvider
import im.vector.app.core.resources.LocaleProvider
import im.vector.app.core.resources.StringProvider
import org.matrix.android.sdk.api.session.pushers.PushersService
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs

private const val DEFAULT_PUSHER_FILE_TAG = "mobile"

class PushersManager @Inject constructor(
        private val activeSessionHolder: ActiveSessionHolder,
        private val localeProvider: LocaleProvider,
        private val stringProvider: StringProvider,
        private val appNameProvider: AppNameProvider
) {
    suspend fun testPush(pushKey: String) {
        val currentSession = activeSessionHolder.getActiveSession()

        currentSession.testPush(
                stringProvider.getString(R.string.pusher_http_url),
                stringProvider.getString(R.string.pusher_app_id),
                pushKey,
                TEST_EVENT_ID
        )
    }

    fun enqueueRegisterPusherWithFcmKey(pushKey: String): UUID {
        val currentSession = activeSessionHolder.getActiveSession()
        return currentSession.enqueueAddHttpPusher(createHttpPusher(pushKey))
    }

    suspend fun registerPusherWithFcmKey(pushKey: String) {
        val currentSession = activeSessionHolder.getActiveSession()
        currentSession.addHttpPusher(createHttpPusher(pushKey))
    }

    private fun createHttpPusher(pushKey: String) = PushersService.HttpPusher(
            pushKey,
            stringProvider.getString(R.string.pusher_app_id),
            profileTag = DEFAULT_PUSHER_FILE_TAG + "_" + abs(activeSessionHolder.getActiveSession().myUserId.hashCode()),
            localeProvider.current().language,
            appNameProvider.getAppName(),
            activeSessionHolder.getActiveSession().sessionParams.deviceId ?: "MOBILE",
            stringProvider.getString(R.string.pusher_http_url),
            append = false,
            withEventIdOnly = true
    )

    suspend fun registerEmailForPush(email: String) {
        val currentSession = activeSessionHolder.getActiveSession()
        val appName = appNameProvider.getAppName()
        currentSession.addEmailPusher(
                email = email,
                lang = localeProvider.current().language,
                emailBranding = appName,
                appDisplayName = appName,
                deviceDisplayName = currentSession.sessionParams.deviceId ?: "MOBILE"
        )
    }

    suspend fun unregisterEmailPusher(email: String) {
        val currentSession = activeSessionHolder.getSafeActiveSession() ?: return
        currentSession.removeEmailPusher(email)
    }

    suspend fun unregisterPusher(pushKey: String) {
        val currentSession = activeSessionHolder.getSafeActiveSession() ?: return
        currentSession.removeHttpPusher(pushKey, stringProvider.getString(R.string.pusher_app_id))
    }

    companion object {
        const val TEST_EVENT_ID = "\$THIS_IS_A_FAKE_EVENT_ID"
    }
}
