

package im.vector.app.ui.robot

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import im.vector.app.R
import im.vector.app.espresso.tools.waitUntilActivityVisible
import im.vector.app.espresso.tools.waitUntilViewVisible
import im.vector.app.features.analytics.ui.consent.AnalyticsOptInActivity

class AnalyticsRobot {

    fun optIn() {
        answerOptIn(true)
    }

    fun optOut() {
        answerOptIn(false)
    }

    private fun answerOptIn(optIn: Boolean) {
        waitUntilActivityVisible<AnalyticsOptInActivity> {
            waitUntilViewVisible(withId(R.id.title))
        }
        assertDisplayed(R.id.title, R.string.analytics_opt_in_title)
        if (optIn) {
            clickOn(R.id.submit)
        } else {
            clickOn(R.id.later)
        }
    }
}
