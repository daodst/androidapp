

package org.matrix.android.sdk.session.room.timeline

import androidx.test.filters.LargeTest
import org.junit.Assert.fail
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.CryptoTestHelper
import java.util.concurrent.CountDownLatch


@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
@Ignore("This test will be ignored until it is fixed")
@LargeTest
class TimelineWithManyMembersTest : InstrumentedTest {

    companion object {
        private const val NUMBER_OF_MEMBERS = 6
    }

    private val commonTestHelper = CommonTestHelper(context())
    private val cryptoTestHelper = CryptoTestHelper(commonTestHelper)

    

    @Test
    fun everyone_should_decrypt_message_in_a_crowded_room() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithManyMembers(NUMBER_OF_MEMBERS)

        val sessionForFirstMember = cryptoTestData.firstSession
        val roomForFirstMember = sessionForFirstMember.getRoom(cryptoTestData.roomId)!!

        val firstMessage = "First messages from Alice"
        commonTestHelper.sendTextMessage(
                roomForFirstMember,
                firstMessage,
                1,
                600_000
        )

        for (index in 1 until cryptoTestData.sessions.size) {
            val session = cryptoTestData.sessions[index]
            val roomForCurrentMember = session.getRoom(cryptoTestData.roomId)!!
            val timelineForCurrentMember = roomForCurrentMember.createTimeline(null, TimelineSettings(30))
            timelineForCurrentMember.start()

            session.startSync(true)

            run {
                val lock = CountDownLatch(1)
                val eventsListener = commonTestHelper.createEventListener(lock) { snapshot ->
                    snapshot
                            .find { it.isEncrypted() }
                            ?.let {
                                val body = it.root.getClearContent()?.toModel<MessageContent>()?.body
                                if (body?.startsWith(firstMessage).orFalse()) {
                                    println("User " + session.myUserId + " decrypted as " + body)
                                    return@createEventListener true
                                } else {
                                    fail("User " + session.myUserId + " decrypted as " + body + " CryptoError: " + it.root.mCryptoError)
                                    false
                                }
                            } ?: return@createEventListener false
                }
                timelineForCurrentMember.addListener(eventsListener)
                commonTestHelper.await(lock, 600_000)
            }
            session.stopSync()
        }
    }
}
