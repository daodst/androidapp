

package im.vector.app.ui.robot.settings

import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.interaction.BaristaDialogInteractions.clickDialogNegativeButton
import im.vector.app.R
import im.vector.app.espresso.tools.waitUntilViewVisible

class SettingsPreferencesRobot {

    fun crawl() {
        clickOn(R.string.settings_interface_language)
        waitUntilViewVisible(withText("Dansk (Danmark)"))
        pressBack()
        clickOn(R.string.settings_theme)
        clickDialogNegativeButton()
        clickOn(R.string.font_size)
        clickDialogNegativeButton()
    }
}
