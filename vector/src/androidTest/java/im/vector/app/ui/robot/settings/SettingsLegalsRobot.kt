

package im.vector.app.ui.robot.settings

import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton
import im.vector.app.R

class SettingsLegalsRobot {

    fun crawl() {
        clickOn(R.string.settings_third_party_notices)
        clickDialogPositiveButton()
    }
}
