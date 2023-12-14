

package org.matrix.android.sdk.session.room.timeline

import androidx.test.filters.LargeTest
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
class TimelinePreviousLastForwardTest : InstrumentedTest {

    

    @Test
    fun previousLastForwardTest() {
        val commonTestHelper = CommonTestHelper(context())
        val cryptoTestHelper = CryptoTestHelper(commonTestHelper)
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoom(false)

        val aliceSession = cryptoTestData.firstSession
        val bobSession = cryptoTestData.secondSession!!
        val aliceRoomId = cryptoTestData.roomId

        aliceSession.cryptoService().setWarnOnUnknownDevices(false)
        bobSession.cryptoService().setWarnOnUnknownDevices(false)

        val roomFromAlicePOV = aliceSession.getRoom(aliceRoomId)!!
        val roomFromBobPOV = bobSession.getRoom(aliceRoomId)!!

        val bobTimeline = roomFromBobPOV.createTimeline(null, TimelineSettings(30))
        bobTimeline.start()

        run {
            val lock = CountDownLatch(1)
            val eventsListener = commonTestHelper.createEventListener(lock) { snapshot ->
                Timber.e("Bob timeline updated: with ${snapshot.size} events:")
                snapshot.forEach {
                    Timber.w(" event ${it.root}")
                }

                
                snapshot.size == 8
            }

            bobTimeline.addListener(eventsListener)
            commonTestHelper.await(lock)
            bobTimeline.removeAllListeners()

            bobTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeFalse()
            bobTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeFalse()
        }

        
        bobSession.stopSync()

        val firstMessage = "First messages from Alice"
        
        val firstMessageFromAliceId = commonTestHelper.sendTextMessage(
                roomFromAlicePOV,
                firstMessage,
                30)
                .last()
                .eventId

        
        bobSession.startSync(true)

        run {
            val lock = CountDownLatch(1)
            val eventsListener = commonTestHelper.createEventListener(lock) { snapshot ->
                Timber.e("Bob timeline updated: with ${snapshot.size} events:")
                snapshot.forEach {
                    Timber.w(" event ${it.root}")
                }

                
                snapshot.size == 10 &&
                        snapshot.all { it.root.content.toModel<MessageContent>()?.body?.startsWith(firstMessage).orFalse() }
            }

            bobTimeline.addListener(eventsListener)
            commonTestHelper.await(lock)
            bobTimeline.removeAllListeners()

            bobTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeTrue()
            bobTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeFalse()
        }

        
        bobSession.stopSync()

        val secondMessage = "Second messages from Alice"
        
        commonTestHelper.sendTextMessage(
                roomFromAlicePOV,
                secondMessage,
                30)

        
        bobSession.startSync(true)

        run {
            val lock = CountDownLatch(1)
            val eventsListener = commonTestHelper.createEventListener(lock) { snapshot ->
                Timber.e("Bob timeline updated: with ${snapshot.size} events:")
                snapshot.forEach {
                    Timber.w(" event ${it.root}")
                }

                
                snapshot.size == 10 &&
                        snapshot.all { it.root.content.toModel<MessageContent>()?.body?.startsWith(secondMessage).orFalse() }
            }

            bobTimeline.addListener(eventsListener)
            commonTestHelper.await(lock)
            bobTimeline.removeAllListeners()

            bobTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeTrue()
            bobTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeFalse()
        }

        
        run {
            val lock = CountDownLatch(1)
            val eventsListener = commonTestHelper.createEventListener(lock) { snapshot ->
                Timber.e("Bob timeline updated: with ${snapshot.size} events:")
                snapshot.forEach {
                    Timber.w(" event ${it.root}")
                }

                
                snapshot.size == 1
            }

            bobTimeline.addListener(eventsListener)

            
            bobTimeline.restartWithEventId(firstMessageFromAliceId)

            commonTestHelper.await(lock)
            bobTimeline.removeAllListeners()

            bobTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeTrue()
            bobTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeTrue()
        }

        
        run {
            val lock = CountDownLatch(1)
            val eventsListener = commonTestHelper.createEventListener(lock) { snapshot ->
                Timber.e("Bob timeline updated: with ${snapshot.size} events:")
                snapshot.forEach {
                    Timber.w(" event ${it.root}")
                }

                snapshot.size == 44 
            }

            bobTimeline.addListener(eventsListener)

            
            bobTimeline.paginate(Timeline.Direction.BACKWARDS, 50)
            
            bobTimeline.paginate(Timeline.Direction.FORWARDS, 35)

            commonTestHelper.await(lock)
            bobTimeline.removeAllListeners()

            bobTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeTrue()
            bobTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeFalse()
        }

        
        run {
            val lock = CountDownLatch(1)
            val eventsListener = commonTestHelper.createEventListener(lock) { snapshot ->
                Timber.e("Bob timeline updated: with ${snapshot.size} events:")
                snapshot.forEach {
                    Timber.w(" event ${it.root}")
                }

                
                snapshot.lastOrNull()?.root?.getClearType() == EventType.STATE_ROOM_CREATE &&
                        
                        snapshot.size == 68 && 
                        snapshot.checkSendOrder(secondMessage, 30, 0) &&
                        snapshot.checkSendOrder(firstMessage, 30, 30)
            }

            bobTimeline.addListener(eventsListener)

            bobTimeline.paginate(Timeline.Direction.FORWARDS, 50)

            commonTestHelper.await(lock)
            bobTimeline.removeAllListeners()

            bobTimeline.hasMoreToLoad(Timeline.Direction.FORWARDS).shouldBeFalse()
            bobTimeline.hasMoreToLoad(Timeline.Direction.BACKWARDS).shouldBeFalse()
        }

        bobTimeline.dispose()

        cryptoTestData.cleanUp(commonTestHelper)
    }
}
