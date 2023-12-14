

package im.vector.app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import im.vector.app.features.MainActivity
import im.vector.app.features.home.HomeActivity
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RegistrationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun simpleRegister() {
        val userId: String = "UiAutoTest_${System.currentTimeMillis()}"
        val password: String = "password"
        val homeServerUrl: String = "http://10.0.2.2:8080"

        
        onView(withId(R.id.loginSplashSubmit))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.login_splash_submit)))

        
        onView(withId(R.id.loginSplashSubmit))
                .perform(click())

        
        onView(withId(R.id.loginServerTitle))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.login_server_title)))

        
        onView(withId(R.id.loginServerChoiceOther))
                .perform(click())

        
        onView(withId(R.id.loginServerUrlFormHomeServerUrl))
                .perform(typeText(homeServerUrl))

        
        onView(withId(R.id.loginServerUrlFormSubmit))
                .check(matches(isEnabled()))
                .perform(closeSoftKeyboard(), click())

        
        onView(withId(R.id.loginSignupSigninSubmit))
                .check(matches(isDisplayed()))
                .perform(click())

        
        onView(withId(R.id.loginField))
                .check(matches(isDisplayed()))
        onView(withId(R.id.passwordField))
                .check(matches(isDisplayed()))

        
        onView(withId(R.id.loginField))
                .perform(typeText(userId))

        
        onView(withId(R.id.loginSubmit))
                .check(matches(not(isEnabled())))

        
        onView(withId(R.id.passwordField))
                .perform(closeSoftKeyboard(), typeText(password))

        
        onView(withId(R.id.loginSubmit))
                .check(matches(isEnabled()))
                .perform(closeSoftKeyboard(), click())

        withIdlingResource(activityIdlingResource(HomeActivity::class.java)) {
            onView(withId(R.id.roomListContainer))
                    .check(matches(isDisplayed()))
        }

        val activity = EspressoHelper.getCurrentActivity()!!
        val uiSession = (activity as HomeActivity).activeSessionHolder.getActiveSession()

        
        withIdlingResource(initialSyncIdlingResource(uiSession)) {
            onView(withId(R.id.roomListContainer))
                    .check(matches(isDisplayed()))
        }
    }
}
