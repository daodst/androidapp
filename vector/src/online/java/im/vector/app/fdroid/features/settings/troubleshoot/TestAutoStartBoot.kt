
package im.vector.app.fdroid.features.settings.troubleshoot

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import im.vector.app.R
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.settings.troubleshoot.TroubleshootTest
import javax.inject.Inject


class TestAutoStartBoot @Inject constructor(private val vectorPreferences: VectorPreferences,
                                            private val stringProvider: StringProvider) :
        TroubleshootTest(R.string.settings_troubleshoot_test_service_boot_title) {

    override fun perform(activityResultLauncher: ActivityResultLauncher<Intent>) {
        if (vectorPreferences.autoStartOnBoot()) {
            description = stringProvider.getString(R.string.settings_troubleshoot_test_service_boot_success)
            status = TestStatus.SUCCESS
            quickFix = null
        } else {
            description = stringProvider.getString(R.string.settings_troubleshoot_test_service_boot_failed)
            quickFix = object : TroubleshootQuickFix(R.string.settings_troubleshoot_test_service_boot_quickfix) {
                override fun doFix() {
                    vectorPreferences.setAutoStartOnBoot(true)
                    manager?.retry(activityResultLauncher)
                }
            }
            status = TestStatus.FAILED
        }
    }
}
