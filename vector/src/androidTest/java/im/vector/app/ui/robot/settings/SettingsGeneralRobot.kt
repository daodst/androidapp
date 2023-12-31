

package im.vector.app.ui.robot.settings

import androidx.test.espresso.Espresso.pressBack
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.interaction.BaristaDialogInteractions.clickDialogNegativeButton
import com.adevinta.android.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton
import im.vector.app.R
import im.vector.app.espresso.tools.clickOnPreference

class SettingsGeneralRobot {

    fun crawl() {
        clickOn(R.string.settings_profile_picture)
        clickDialogPositiveButton()
        clickOn(R.string.settings_display_name)
        clickDialogNegativeButton()
        clickOn(R.string.settings_password)
        clickDialogNegativeButton()
        clickOn(R.string.settings_emails_and_phone_numbers_title)
        pressBack()
        clickOn(R.string.settings_discovery_manage)
        clickOn(R.string.add_identity_server)
        pressBack()
        pressBack()
        
        clickOnPreference(R.string.settings_home_server)
        pressBack()
        
        clickOnPreference(R.string.settings_identity_server)
        pressBack()
        
        clickOnPreference(R.string.settings_deactivate_my_account)
        pressBack()
    }
}
