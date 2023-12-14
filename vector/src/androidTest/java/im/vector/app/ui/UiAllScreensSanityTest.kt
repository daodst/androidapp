

package im.vector.app.ui

import android.Manifest
import androidx.test.espresso.IdlingPolicies
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import im.vector.app.R
import im.vector.app.espresso.tools.ScreenshotFailureRule
import im.vector.app.features.MainActivity
import im.vector.app.getString
import im.vector.app.ui.robot.ElementRobot
import im.vector.app.ui.robot.settings.labs.LabFeature
import im.vector.app.ui.robot.withDeveloperMode
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.util.UUID
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
@LargeTest
class UiAllScreensSanityTest {

    @get:Rule
    val testRule = RuleChain
            .outerRule(ActivityScenarioRule(MainActivity::class.java))
            .around(GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            .around(ScreenshotFailureRule())

    private val elementRobot = ElementRobot()

    
    
    
    
    @Test
    fun allScreensTest() {
        IdlingPolicies.setMasterPolicyTimeout(120, TimeUnit.SECONDS)

        elementRobot.onboarding {
            crawl()
        }

        
        val userId = "UiTest_" + UUID.randomUUID().toString()
        elementRobot.signUp(userId)

        elementRobot.settings {
            general { crawl() }
            notifications { crawl() }
            preferences { crawl() }
            voiceAndVideo()
            securityAndPrivacy { crawl() }
            labs()
            advancedSettings { crawl() }
            helpAndAbout { crawl() }
            legals { crawl() }
        }

        elementRobot.newDirectMessage {
            verifyQrCodeButton()
            verifyInviteFriendsButton()
        }

        elementRobot.newRoom {
            createNewRoom {
                crawl()
                createRoom {
                    val message = "Hello world!"
                    postMessage(message)
                    crawl()
                    crawlMessage(message)
                    openSettings { crawl() }
                }
            }
        }

        testThreadScreens()

        elementRobot.space {
            createSpace {
                crawl()
            }
            val spaceName = UUID.randomUUID().toString()
            createSpace {
                createPublicSpace(spaceName)
            }

            spaceMenu(spaceName) {
                spaceMembers()
                spaceSettings {
                    crawl()
                }
                exploreRooms()

                invitePeople().also { openMenu(spaceName) }
                addRoom().also { openMenu(spaceName) }
                addSpace().also { openMenu(spaceName) }

                leaveSpace()
            }
        }

        elementRobot.withDeveloperMode {
            settings {
                advancedSettings { crawlDeveloperOptions() }
            }
            roomList {
                openRoom(getString(R.string.room_displayname_empty_room)) {
                    val message = "Test view source"
                    postMessage(message)
                    openMessageMenu(message) {
                        viewSource()
                    }
                }
            }
        }

        elementRobot.roomList {
            verifyCreatedRoom()
        }

        elementRobot.signout(expectSignOutWarning = true)

        
        elementRobot.login(userId)
        elementRobot.dismissVerificationIfPresent()
        
        elementRobot.signout(expectSignOutWarning = false)
    }

    
    private fun testThreadScreens() {
        elementRobot.toggleLabFeature(LabFeature.THREAD_MESSAGES)
        elementRobot.newRoom {
            createNewRoom {
                crawl()
                createRoom {
                    val message = "Hello This message will be a thread!"
                    postMessage(message)
                    replyToThread(message)
                    viewInRoom(message)
                    openThreadSummaries()
                    selectThreadSummariesFilter()
                }
            }
        }
        elementRobot.toggleLabFeature(LabFeature.THREAD_MESSAGES)
    }
}
