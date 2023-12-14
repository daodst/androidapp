

package im.vector.app.espresso.tools

import android.app.Activity
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import im.vector.app.activityIdlingResource
import im.vector.app.waitForView
import im.vector.app.withIdlingResource
import org.hamcrest.Matcher

inline fun <reified T : Activity> waitUntilActivityVisible(noinline block: (() -> Unit) = {}) {
    withIdlingResource(activityIdlingResource(T::class.java), block)
}

fun waitUntilViewVisible(viewMatcher: Matcher<View>) {
    onView(ViewMatchers.isRoot()).perform(waitForView(viewMatcher))
}

fun waitUntilDialogVisible(viewMatcher: Matcher<View>) {
    onView(viewMatcher).inRoot(isDialog()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    waitUntilViewVisible(viewMatcher)
}
