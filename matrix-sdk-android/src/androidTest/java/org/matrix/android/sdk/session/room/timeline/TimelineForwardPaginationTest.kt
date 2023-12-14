

package org.matrix.android.sdk.session.room.timeline

import androidx.test.filters.LargeTest
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.internal.assertEquals
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.CryptoTestHelper
import org.matrix.android.sdk.common.checkSendOrder
import timber.log.Timber
import java.util.concurrent.CountDownLatch

@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
@LargeTest
class TimelineForwardPaginationTest : InstrumentedTest {


    
    @Test
    fun forwardPaginationTest() {
        val commonTestHelper = CommonTestHelper(context())
        val cryptoTestHelper = CryptoTestHelper(commonTestHelper)
        val numberOfMessagesToSend = 90
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceInARoom(false)

        val aliceSession = cryptoTestData.firstSession
        val aliceRoomId = cryptoTestData.roomId

        aliceSession.cryptoService().setWarnOnUnknownDevices(false)

        val roomFromAlicePOV = aliceSession.getRoom(aliceRoomId)!!

        
        val message = "Message from Alice"
        val sentMessages = commonTestHelper.sendTextMessage(
                roomFromAlicePOV,
                message,
                numberOfMessagesToSend)

        
        commonTestHelper.clearCacheAndSync(aliceSession)
        val aliceTimeline = roomFromAlicePOV.createTimeline(null, TimelineSettings(30))
        aliceTimeline.start()

        
        run {
            val lock = CountDownLatch(1)
            val eventsListener = commonTestHelper.createEventListener(lock) { snapshot ->
                Timber.e("Alice timeline updated: with ${snapshot.size} events:")
                snapshot.forEach {
                    Timber.w(" event ${it.root.content}")
                }

                
                snapshot.size == 10 &&
                        snapshot.all { it.root.content.toModel<MessageContent>()?.body?.startsWith(message).orFalse() }
            }

            
            aliceTimeline.addListener(eventsListener)
            commonTestHelper.await(lock)
            aliceTimeline.removeAllListeners()

            aliceTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeTrue()
            aliceTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeFalse()
        }

        
        
        run {
            val lock = CountDownLatch(1)
            val aliceEventsListener = commonTestHelper.createEventListener(lock) { snapshot ->
                Timber.e("Alice timeline updated: with ${snapshot.size} events:")
                snapshot.forEach {
                    Timber.w(" event ${it.root.content}")
                }

                
                snapshot.size == 1 &&
                        snapshot.all { it.root.content.toModel<MessageContent>()?.body?.startsWith("Message from Alice").orFalse() }
            }

            aliceTimeline.addListener(aliceEventsListener)

            
            aliceTimeline.restartWithEventId(sentMessages.last().eventId)

            commonTestHelper.await(lock)
            aliceTimeline.removeAllListeners()

            aliceTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeTrue()
            aliceTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeTrue()
        }

        
        
        run {
            val snapshot = runBlocking {
                aliceTimeline.awaitPaginate(Timeline.Direction.BACKWARDS, 50)
                aliceTimeline.awaitPaginate(Timeline.Direction.FORWARDS, 50)
            }
            aliceTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeTrue()
            aliceTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeFalse()

            assertEquals(EventType.STATE_ROOM_CREATE, snapshot.lastOrNull()?.root?.getClearType())
            
            
            assertEquals(57, snapshot.size)
        }

        
        
        run {
            
            val snapshot = runBlocking {
                aliceTimeline.awaitPaginate(Timeline.Direction.FORWARDS, 50)
            }
            
            snapshot.size == 6 + numberOfMessagesToSend &&
                    snapshot.checkSendOrder(message, numberOfMessagesToSend, 0)

            
            aliceTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeFalse()
            aliceTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeFalse()
        }

        aliceTimeline.dispose()

        cryptoTestData.cleanUp(commonTestHelper)
    }
}
