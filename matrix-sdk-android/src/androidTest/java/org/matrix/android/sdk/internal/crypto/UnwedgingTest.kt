

package org.matrix.android.sdk.internal.crypto

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.amshove.kluent.shouldBe
import org.junit.Assert
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.UserPasswordAuth
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.EncryptedEventContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.CryptoTestHelper
import org.matrix.android.sdk.common.TestConstants
import org.matrix.android.sdk.internal.crypto.model.OlmSessionWrapper
import org.matrix.android.sdk.internal.crypto.store.db.deserializeFromRealm
import org.matrix.android.sdk.internal.crypto.store.db.serializeForRealm
import org.matrix.olm.OlmSession
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
class UnwedgingTest : InstrumentedTest {

    private lateinit var messagesReceivedByBob: List<TimelineEvent>
    private val testHelper = CommonTestHelper(context())
    private val cryptoTestHelper = CryptoTestHelper(testHelper)

    @Before
    fun init() {
        messagesReceivedByBob = emptyList()
    }

    
    @Test
    fun testUnwedging() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoom()

        val aliceSession = cryptoTestData.firstSession
        val aliceRoomId = cryptoTestData.roomId
        val bobSession = cryptoTestData.secondSession!!

        val aliceCryptoStore = (aliceSession.cryptoService() as DefaultCryptoService).cryptoStoreForTesting
        val olmDevice = (aliceSession.cryptoService() as DefaultCryptoService).olmDeviceForTest

        val roomFromBobPOV = bobSession.getRoom(aliceRoomId)!!
        val roomFromAlicePOV = aliceSession.getRoom(aliceRoomId)!!

        val bobTimeline = roomFromBobPOV.createTimeline(null, TimelineSettings(20))
        bobTimeline.start()

        val bobFinalLatch = CountDownLatch(1)
        val bobHasThreeDecryptedEventsListener = object : Timeline.Listener {
            override fun onTimelineFailure(throwable: Throwable) {
                
            }

            override fun onNewTimelineEvents(eventIds: List<String>) {
                
            }

            override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
                val decryptedEventReceivedByBob = snapshot.filter { it.root.type == EventType.ENCRYPTED }
                Timber.d("Bob can now decrypt ${decryptedEventReceivedByBob.size} messages")
                if (decryptedEventReceivedByBob.size == 3) {
                    if (decryptedEventReceivedByBob[0].root.mCryptoError == MXCryptoError.ErrorType.UNKNOWN_INBOUND_SESSION_ID) {
                        bobFinalLatch.countDown()
                    }
                }
            }
        }
        bobTimeline.addListener(bobHasThreeDecryptedEventsListener)

        var latch = CountDownLatch(1)
        var bobEventsListener = createEventListener(latch, 1)
        bobTimeline.addListener(bobEventsListener)
        messagesReceivedByBob = emptyList()

        
        roomFromAlicePOV.sendTextMessage("First message")

        
        testHelper.await(latch)
        bobTimeline.removeListener(bobEventsListener)

        messagesReceivedByBob.size shouldBe 1
        val firstMessageSession = messagesReceivedByBob[0].root.content.toModel<EncryptedEventContent>()!!.sessionId!!

        
        
        
        val sessionIdsForBob = aliceCryptoStore.getDeviceSessionIds(bobSession.cryptoService().getMyDevice().identityKey()!!)
        sessionIdsForBob!!.size shouldBe 1
        val olmSession = aliceCryptoStore.getDeviceSession(sessionIdsForBob.first(), bobSession.cryptoService().getMyDevice().identityKey()!!)!!

        val oldSession = serializeForRealm(olmSession.olmSession)

        aliceSession.cryptoService().discardOutboundSession(roomFromAlicePOV.roomId)
        Thread.sleep(6_000)

        latch = CountDownLatch(1)
        bobEventsListener = createEventListener(latch, 2)
        bobTimeline.addListener(bobEventsListener)
        messagesReceivedByBob = emptyList()

        Timber.i("## CRYPTO | testUnwedging:  Alice sends a 2nd message with a 2nd megolm session")
        
        roomFromAlicePOV.sendTextMessage("Second message")

        
        testHelper.await(latch)
        bobTimeline.removeListener(bobEventsListener)

        messagesReceivedByBob.size shouldBe 2
        
        val secondMessageSession = messagesReceivedByBob[0].root.content.toModel<EncryptedEventContent>()!!.sessionId!!
        Assert.assertNotEquals(firstMessageSession, secondMessageSession)

        
        Timber.i("## CRYPTO | testUnwedging: wedge the session now. Set crypto state like after the first message")

        aliceCryptoStore.storeSession(OlmSessionWrapper(deserializeFromRealm<OlmSession>(oldSession)!!), bobSession.cryptoService().getMyDevice().identityKey()!!)
        olmDevice.clearOlmSessionCache()
        Thread.sleep(6_000)

        
        aliceSession.cryptoService().discardOutboundSession(roomFromAlicePOV.roomId)

        
        testHelper.waitWithLatch {
            bobEventsListener = createEventListener(it, 3)
            bobTimeline.addListener(bobEventsListener)
            messagesReceivedByBob = emptyList()

            Timber.i("## CRYPTO | testUnwedging: Alice sends a 3rd message with a 3rd megolm session but a wedged olm session")
            
            roomFromAlicePOV.sendTextMessage("Third message")
            
        }
        bobTimeline.removeListener(bobEventsListener)

        messagesReceivedByBob.size shouldBe 3

        val thirdMessageSession = messagesReceivedByBob[0].root.content.toModel<EncryptedEventContent>()!!.sessionId!!
        Timber.i("## CRYPTO | testUnwedging: third message session ID $thirdMessageSession")
        Assert.assertNotEquals(secondMessageSession, thirdMessageSession)

        Assert.assertEquals(EventType.ENCRYPTED, messagesReceivedByBob[0].root.getClearType())
        Assert.assertEquals(EventType.MESSAGE, messagesReceivedByBob[1].root.getClearType())
        Assert.assertEquals(EventType.MESSAGE, messagesReceivedByBob[2].root.getClearType())
        
        testHelper.await(bobFinalLatch)
        bobTimeline.removeListener(bobHasThreeDecryptedEventsListener)

        
        testHelper.doSync<Unit> {
            bobSession.cryptoService().crossSigningService()
                    .initializeCrossSigning(
                            object : UserInteractiveAuthInterceptor {
                                override fun performStage(flowResponse: RegistrationFlowResponse, errCode: String?, promise: Continuation<UIABaseAuth>) {
                                    promise.resume(
                                            UserPasswordAuth(
                                                    user = bobSession.myUserId,
                                                    password = TestConstants.PASSWORD,
                                                    session = flowResponse.session
                                            )
                                    )
                                }
                            }, it)
        }

        
        testHelper.waitWithLatch {
            testHelper.retryPeriodicallyWithLatch(it) {
                
                val result = testHelper.runBlockingTest {
                    tryOrNull {
                        bobSession.cryptoService().decryptEvent(messagesReceivedByBob[0].root, "")
                    }
                }
                Timber.i("## CRYPTO | testUnwedging: decrypt result  ${result?.clearEvent}")
                result != null
            }
        }

        bobTimeline.dispose()

        cryptoTestData.cleanUp(testHelper)
    }

    private fun createEventListener(latch: CountDownLatch, expectedNumberOfMessages: Int): Timeline.Listener {
        return object : Timeline.Listener {
            override fun onTimelineFailure(throwable: Throwable) {
                
            }

            override fun onNewTimelineEvents(eventIds: List<String>) {
                
            }

            override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
                messagesReceivedByBob = snapshot.filter { it.root.type == EventType.ENCRYPTED }

                if (messagesReceivedByBob.size == expectedNumberOfMessages) {
                    latch.countDown()
                }
            }
        }
    }
}
