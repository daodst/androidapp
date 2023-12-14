
package im.vector.app.features.settings.troubleshoot

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.utils.startNotificationSettingsIntent
import im.vector.app.features.notifications.NotificationUtils
import javax.inject.Inject


class TestNotification @Inject constructor(private val context: Context,
                                           private val notificationUtils: NotificationUtils,
                                           private val stringProvider: StringProvider) :
        TroubleshootTest(R.string.settings_troubleshoot_test_notification_title) {

    override fun perform(activityResultLauncher: ActivityResultLauncher<Intent>) {
        
        notificationUtils.displayDiagnosticNotification()
        description = stringProvider.getString(R.string.settings_troubleshoot_test_notification_notice)

        quickFix = object : TroubleshootQuickFix(R.string.open_settings) {
            override fun doFix() {
                startNotificationSettingsIntent(context, activityResultLauncher)
            }
        }

        status = TestStatus.WAITING_FOR_USER
    }

    override fun onNotificationClicked() {
        description = stringProvider.getString(R.string.settings_troubleshoot_test_notification_notification_clicked)
        quickFix = null
        status = TestStatus.SUCCESS
    }
}
