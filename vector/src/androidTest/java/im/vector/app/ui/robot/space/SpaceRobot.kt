

package im.vector.app.ui.robot.space

import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.interaction.BaristaDrawerInteractions.openDrawer
import im.vector.app.R

class SpaceRobot {

    fun createSpace(block: SpaceCreateRobot.() -> Unit) {
        openDrawer()
        clickOn(R.string.create_space)
        block(SpaceCreateRobot())
    }

    fun spaceMenu(spaceName: String, block: SpaceMenuRobot.() -> Unit) {
        openDrawer()
        with(SpaceMenuRobot()) {
            openMenu(spaceName)
            block()
        }
    }
}
