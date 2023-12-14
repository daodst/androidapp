

package im.vector.app.ui.robot

import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.interaction.BaristaDialogInteractions.clickDialogNegativeButton
import com.adevinta.android.barista.interaction.BaristaListInteractions.clickListItem
import im.vector.app.R
import im.vector.app.espresso.tools.waitUntilActivityVisible
import im.vector.app.espresso.tools.waitUntilDialogVisible
import im.vector.app.espresso.tools.waitUntilViewVisible
import im.vector.app.features.roommemberprofile.RoomMemberProfileActivity

class RoomSettingsRobot {

    fun crawl() {
        
        clickListItem(R.id.matrixProfileRecyclerView, 3)
        navigateToRoomParameters()
        pressBack()

        
        clickListItem(R.id.matrixProfileRecyclerView, 5)
        pressBack()

        assertDisplayed(R.id.roomProfileAvatarView)

        
        clickListItem(R.id.matrixProfileRecyclerView, 7)
        assertDisplayed(R.id.inviteUsersButton)
        navigateToRoomPeople()
        
        navigateToInvite()
        pressBack()
        pressBack()

        assertDisplayed(R.id.roomProfileAvatarView)

        
        clickListItem(R.id.matrixProfileRecyclerView, 9)
        
        clickOn(R.string.uploads_files_title)
        waitUntilViewVisible(withText(R.string.uploads_media_title))
        pressBack()
        waitUntilViewVisible(withId(R.id.matrixProfileRecyclerView))

        assertDisplayed(R.id.roomProfileAvatarView)

        
        leaveRoom {
            negativeAction()
        }

        
        

        clickListItem(R.id.matrixProfileRecyclerView, 15)
        waitUntilViewVisible(withText(R.string.room_alias_published_alias_title))
        pressBack()

        
        clickListItem(R.id.matrixProfileRecyclerView, 17)
        waitUntilViewVisible(withText(R.string.room_permissions_change_room_avatar))
        clickOn(R.string.room_permissions_change_room_avatar)
        waitUntilDialogVisible(withId(android.R.id.button2))
        clickDialogNegativeButton()
        waitUntilViewVisible(withText(R.string.room_permissions_title))
        
        clickOn(R.string.show_advanced)
        clickOn(R.string.hide_advanced)
        pressBack()

        
        
        
    }

    private fun leaveRoom(block: DialogRobot.() -> Unit) {
        clickListItem(R.id.matrixProfileRecyclerView, 13)
        waitUntilDialogVisible(withId(android.R.id.button2))
        val dialogRobot = DialogRobot()
        block(dialogRobot)
        if (dialogRobot.returnedToPreviousScreen) {
            waitUntilViewVisible(withId(R.id.matrixProfileRecyclerView))
        }
    }

    private fun navigateToRoomParameters() {
        
        clickListItem(R.id.roomSettingsRecyclerView, 4)
        pressBack()

        
        clickListItem(R.id.roomSettingsRecyclerView, 6)
        pressBack()
    }

    private fun navigateToInvite() {
        assertDisplayed(R.id.inviteUsersButton)
        clickOn(R.id.inviteUsersButton)
        ViewActions.closeSoftKeyboard()
        pressBack()
    }

    private fun navigateToRoomPeople() {
        
        clickListItem(R.id.roomSettingsRecyclerView, 1)
        waitUntilActivityVisible<RoomMemberProfileActivity> {
            waitUntilViewVisible(withId(R.id.memberProfilePowerLevelView))
        }

        
        clickListItem(R.id.matrixProfileRecyclerView, 1)
        waitUntilViewVisible(withId(R.id.bottomSheetRecyclerView))
        pressBack()
        waitUntilViewVisible(withId(R.id.matrixProfileRecyclerView))

        
        clickListItem(R.id.matrixProfileRecyclerView, 3)
        waitUntilDialogVisible(withId(android.R.id.button2))
        clickDialogNegativeButton()
        waitUntilViewVisible(withId(R.id.matrixProfileRecyclerView))
        pressBack()
    }
}
