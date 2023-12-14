

package im.vector.app.ui.robot

import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import im.vector.app.R
import im.vector.app.waitForView

class NewDirectMessageRobot {

    fun verifyQrCodeButton() {
        Espresso.onView(ViewMatchers.withId(R.id.userListRecyclerView))
                .perform(waitForView(ViewMatchers.withText(R.string.qr_code)))
    }

    fun verifyInviteFriendsButton() {
        Espresso.onView(ViewMatchers.withId(R.id.userListRecyclerView))
                .perform(waitForView(ViewMatchers.withText(R.string.invite_friends)))
    }
}
