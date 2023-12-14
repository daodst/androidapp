

package im.vector.app.ui.robot.settings

import androidx.test.espresso.Espresso
import im.vector.app.R
import im.vector.app.clickOnAndGoBack
import im.vector.app.espresso.tools.clickOnPreference

class SettingsSecurityRobot {

    fun crawl() {
        clickOnPreference(R.string.settings_active_sessions_show_all)
        Espresso.pressBack()

        clickOnPreference(R.string.encryption_message_recovery)
        
        Espresso.pressBack()
        

        clickOnPreference(R.string.settings_opt_in_of_analytics)
        Espresso.pressBack()

        ignoredUsers()
    }

    private fun ignoredUsers(block: () -> Unit = {}) {
        clickOnAndGoBack(R.string.settings_ignored_users) { block() }
    }
}
