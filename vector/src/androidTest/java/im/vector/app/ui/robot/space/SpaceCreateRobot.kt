

package im.vector.app.ui.robot.space

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import im.vector.app.R
import im.vector.app.espresso.tools.waitUntilActivityVisible
import im.vector.app.espresso.tools.waitUntilDialogVisible
import im.vector.app.espresso.tools.waitUntilViewVisible
import im.vector.app.features.home.HomeActivity
import im.vector.app.features.spaces.manage.SpaceManageActivity
import java.util.UUID

class SpaceCreateRobot {

    fun crawl() {
        
        clickOn(R.id.publicButton)
        waitUntilViewVisible(withId(R.id.recyclerView))
        onView(ViewMatchers.withHint(R.string.create_room_name_hint)).perform(ViewActions.replaceText(UUID.randomUUID().toString()))
        clickOn(R.id.nextButton)
        waitUntilViewVisible(withId(R.id.recyclerView))
        pressBack()
        pressBack()

        
        clickOn(R.id.privateButton)
        waitUntilViewVisible(withId(R.id.recyclerView))
        clickOn(R.id.nextButton)

        waitUntilViewVisible(withId(R.id.teammatesButton))
        
        clickOn(R.id.teammatesButton)
        waitUntilViewVisible(withId(R.id.recyclerView))
        clickOn(R.id.nextButton)
        pressBack()
        pressBack()

        
        waitUntilViewVisible(withId(R.id.justMeButton))
        clickOn(R.id.justMeButton)
        waitUntilActivityVisible<SpaceManageActivity> {
            waitUntilViewVisible(withId(R.id.roomList))
        }

        onView(withId(R.id.roomList))
                .perform(
                        RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                                ViewMatchers.hasDescendant(withText(R.string.room_displayname_empty_room)),
                                click()
                        ).atPosition(0)
                )
        clickOn(R.id.spaceAddRoomSaveItem)
        waitUntilActivityVisible<HomeActivity> {
            waitUntilViewVisible(withId(R.id.roomListContainer))
        }
    }

    fun createPublicSpace(spaceName: String) {
        clickOn(R.id.publicButton)
        waitUntilViewVisible(withId(R.id.recyclerView))
        onView(ViewMatchers.withHint(R.string.create_room_name_hint)).perform(ViewActions.replaceText(spaceName))
        clickOn(R.id.nextButton)
        waitUntilViewVisible(withId(R.id.recyclerView))
        clickOn(R.id.nextButton)
        waitUntilDialogVisible(withId(R.id.inviteByMxidButton))
        
        pressBack()
        waitUntilViewVisible(withId(R.id.timelineRecyclerView))
        
        pressBack()
        waitUntilViewVisible(withId(R.id.roomListContainer))
    }
}
