

package im.vector.app.ui.robot.settings

import androidx.test.espresso.Espresso.pressBack
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import im.vector.app.R
import im.vector.app.espresso.tools.clickOnPreference

class SettingsNotificationsRobot {

    fun crawl() {
        clickOn(R.string.settings_notification_default)
        pressBack()
        clickOn(R.string.settings_notification_mentions_and_keywords)
        
        pressBack()
        clickOn(R.string.settings_notification_other)
        pressBack()

        
        clickOnPreference(R.string.settings_notification_troubleshoot)
        pressBack()
    }
}
