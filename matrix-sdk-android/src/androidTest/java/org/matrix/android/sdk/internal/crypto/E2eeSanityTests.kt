

package org.matrix.android.sdk.internal.crypto

import android.util.Log
import androidx.test.filters.LargeTest
import kotlinx.coroutines.delay
import org.amshove.kluent.fail
import org.amshove.kluent.internal.assertEquals
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersion
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersionResult
import org.matrix.android.sdk.api.session.crypto.keysbackup.MegolmBackupCreationInfo
import org.matrix.android.sdk.api.session.crypto.model.ImportRoomKeysResult
import org.matrix.android.sdk.api.session.crypto.model.OlmDecryptionResult
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.content.EncryptedEventContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.failure.JoinRoomFailure
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.CryptoTestHelper
import org.matrix.android.sdk.common.SessionTestParams
import org.matrix.android.sdk.common.TestConstants
import org.matrix.android.sdk.common.TestMatrixCallback

@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
@LargeTest
class E2eeSanityTests : InstrumentedTest {

    private val testHelper = CommonTestHelper(context())
    private val cryptoTestHelper = CryptoTestHelper(testHelper)

    
    @Test
    fun testSendingE2EEMessages() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoom(true)
        val aliceSession = cryptoTestData.firstSession
        val e2eRoomID = cryptoTestData.roomId

        val aliceRoomPOV = aliceSession.getRoom(e2eRoomID)!!

        
        val otherAccounts = listOf("benoit", "valere", "ganfra") 
                .map {
                    testHelper.createAccount(it, SessionTestParams(true))
                }

        Log.v("#E2E TEST", "All accounts created")
        
        otherAccounts.forEach {
            testHelper.runBlockingTest {
                Log.v("#E2E TEST", "Alice invites ${it.myUserId}")
                aliceRoomPOV.invite(it.myUserId)
            }
        }

        
        otherAccounts.forEach { otherSession ->
            waitForAndAcceptInviteInRoom(otherSession, e2eRoomID)
            Log.v("#E2E TEST", "${otherSession.myUserId} joined room $e2eRoomID")
        }

        
        ensureMembersHaveJoined(aliceSession, otherAccounts, e2eRoomID)

        Log.v("#E2E TEST", "All users have joined the room")
        Log.v("#E2E TEST", "Alice is sending the message")

        val text = "This is my message"
        val sentEventId: String? = sendMessageInRoom(aliceRoomPOV, text)
        
        Assert.assertTrue("Message should be sent", sentEventId != null)

        
        otherAccounts.forEach { otherSession ->
            testHelper.waitWithLatch { latch ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val timelineEvent = otherSession.getRoom(e2eRoomID)?.getTimelineEvent(sentEventId!!)
                    timelineEvent != null &&
                            timelineEvent.isEncrypted() &&
                            timelineEvent.root.getClearType() == EventType.MESSAGE
                }
            }
        }

        
        val newAccount = listOf("adam") 
                .map {
                    testHelper.createAccount(it, SessionTestParams(true))
                }

        newAccount.forEach {
            testHelper.runBlockingTest {
                Log.v("#E2E TEST", "Alice invites ${it.myUserId}")
                aliceRoomPOV.invite(it.myUserId)
            }
        }

        newAccount.forEach {
            waitForAndAcceptInviteInRoom(it, e2eRoomID)
        }

        ensureMembersHaveJoined(aliceSession, newAccount, e2eRoomID)

        
        testHelper.runBlockingTest {
            delay(3_000)
        }

        
        newAccount.forEach { otherSession ->
            testHelper.waitWithLatch { latch ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val timelineEvent = otherSession.getRoom(e2eRoomID)?.getTimelineEvent(sentEventId!!).also {
                        Log.v("#E2E TEST", "Event seen by new user ${it?.root?.getClearType()}|${it?.root?.mCryptoError}")
                    }
                    timelineEvent != null &&
                            timelineEvent.root.getClearType() == EventType.ENCRYPTED &&
                            timelineEvent.root.mCryptoError == MXCryptoError.ErrorType.UNKNOWN_INBOUND_SESSION_ID
                }
            }
        }

        
        Log.v("#E2E TEST", "Alice sends a new message")

        val secondMessage = "2 This is my message"
        val secondSentEventId: String? = sendMessageInRoom(aliceRoomPOV, secondMessage)

        
        newAccount.forEach { otherSession ->
            testHelper.waitWithLatch { latch ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val timelineEvent = otherSession.getRoom(e2eRoomID)?.getTimelineEvent(secondSentEventId!!).also {
                        Log.v("#E2E TEST", "Second Event seen by new user ${it?.root?.getClearType()}|${it?.root?.mCryptoError}")
                    }
                    timelineEvent != null &&
                            timelineEvent.root.getClearType() == EventType.MESSAGE &&
                            secondMessage == timelineEvent.root.getClearContent().toModel<MessageContent>()?.body
                }
            }
        }

        otherAccounts.forEach {
            testHelper.signOutAndClose(it)
        }
        newAccount.forEach { testHelper.signOutAndClose(it) }

        cryptoTestData.cleanUp(testHelper)
    }

    
    @Test
    fun testBasicBackupImport() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoom(true)
        val aliceSession = cryptoTestData.firstSession
        val bobSession = cryptoTestData.secondSession!!
        val e2eRoomID = cryptoTestData.roomId

        Log.v("#E2E TEST", "Create and start key backup for bob ...")
        val bobKeysBackupService = bobSession.cryptoService().keysBackupService()
        val keyBackupPassword = "FooBarBaz"
        val megolmBackupCreationInfo = testHelper.doSync<MegolmBackupCreationInfo> {
            bobKeysBackupService.prepareKeysBackupVersion(keyBackupPassword, null, it)
        }
        val version = testHelper.doSync<KeysVersion> {
            bobKeysBackupService.createKeysBackupVersion(megolmBackupCreationInfo, it)
        }
        Log.v("#E2E TEST", "... Key backup started and enabled for bob")
        

        val aliceRoomPOV = aliceSession.getRoom(e2eRoomID)!!

        
        val sentEventIds = mutableListOf<String>()
        val messagesText = listOf("1. Hello", "2. Bob", "3. Good morning")
        messagesText.forEach { text ->
            val sentEventId = sendMessageInRoom(aliceRoomPOV, text)!!.also {
                sentEventIds.add(it)
            }

            testHelper.waitWithLatch { latch ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val timelineEvent = bobSession.getRoom(e2eRoomID)?.getTimelineEvent(sentEventId)
                    timelineEvent != null &&
                            timelineEvent.isEncrypted() &&
                            timelineEvent.root.getClearType() == EventType.MESSAGE
                }
            }
            
            aliceSession.cryptoService().discardOutboundSession(e2eRoomID)

            testHelper.runBlockingTest {
                delay(1_000)
            }
        }
        Log.v("#E2E TEST", "Bob received all and can decrypt")

        

        Log.v("#E2E TEST", "Force key backup for Bob...")
        testHelper.waitWithLatch { latch ->
            bobKeysBackupService.backupAllGroupSessions(
                    null,
                    TestMatrixCallback(latch, true)
            )
        }
        Log.v("#E2E TEST", "... Key backup done for Bob")

        

        val bobUserId = bobSession.myUserId
        Log.v("#E2E TEST", "Logout alice and bob...")
        testHelper.signOutAndClose(aliceSession)
        testHelper.signOutAndClose(bobSession)
        Log.v("#E2E TEST", "..Logout alice and bob...")

        testHelper.runBlockingTest {
            delay(1_000)
        }

        
        Log.v("#E2E TEST", "Create a new session for Bob")
        val newBobSession = testHelper.logIntoAccount(bobUserId, SessionTestParams(true))

        
        Log.v("#E2E TEST", "check that bob can't currently decrypt")
        sentEventIds.forEach { sentEventId ->
            testHelper.waitWithLatch { latch ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val timelineEvent = newBobSession.getRoom(e2eRoomID)?.getTimelineEvent(sentEventId)?.also {
                        Log.v("#E2E TEST", "Event seen by new user ${it.root.getClearType()}|${it.root.mCryptoError}")
                    }
                    timelineEvent != null &&
                            timelineEvent.root.getClearType() == EventType.ENCRYPTED
                }
            }
        }
        
        ensureCannotDecrypt(sentEventIds, newBobSession, e2eRoomID, MXCryptoError.ErrorType.UNKNOWN_INBOUND_SESSION_ID)

        

        newBobSession.cryptoService().keysBackupService().let { keysBackupService ->
            val keyVersionResult = testHelper.doSync<KeysVersionResult?> {
                keysBackupService.getVersion(version.version, it)
            }

            val importedResult = testHelper.doSync<ImportRoomKeysResult> {
                keysBackupService.restoreKeyBackupWithPassword(keyVersionResult!!,
                        keyBackupPassword,
                        null,
                        null,
                        null, it)
            }

            assertEquals(3, importedResult.totalNumberOfKeys)
        }

        
        ensureCanDecrypt(sentEventIds, newBobSession, e2eRoomID, messagesText)

        testHelper.signOutAndClose(newBobSession)
    }

    
    @Test
    fun testSimpleGossip() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoom(true)
        val aliceSession = cryptoTestData.firstSession
        val bobSession = cryptoTestData.secondSession!!
        val e2eRoomID = cryptoTestData.roomId

        val aliceRoomPOV = aliceSession.getRoom(e2eRoomID)!!

        cryptoTestHelper.initializeCrossSigning(bobSession)

        
        val sentEventIds = mutableListOf<String>()
        val messagesText = listOf("1. Hello", "2. Bob")

        Log.v("#E2E TEST", "Alice sends some messages")
        messagesText.forEach { text ->
            val sentEventId = sendMessageInRoom(aliceRoomPOV, text)!!.also {
                sentEventIds.add(it)
            }

            testHelper.waitWithLatch { latch ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val timelineEvent = bobSession.getRoom(e2eRoomID)?.getTimelineEvent(sentEventId)
                    timelineEvent != null &&
                            timelineEvent.isEncrypted() &&
                            timelineEvent.root.getClearType() == EventType.MESSAGE
                }
            }
        }

        
        ensureIsDecrypted(sentEventIds, bobSession, e2eRoomID)

        
        
        Log.v("#E2E TEST", "Create a new session for Bob")
        val newBobSession = testHelper.logIntoAccount(bobSession.myUserId, SessionTestParams(true))

        
        Log.v("#E2E TEST", "check that new bob can't currently decrypt")

        ensureCannotDecrypt(sentEventIds, newBobSession, e2eRoomID, MXCryptoError.ErrorType.UNKNOWN_INBOUND_SESSION_ID)

        
        sentEventIds.forEach { sentEventId ->
            val event = newBobSession.getRoom(e2eRoomID)!!.getTimelineEvent(sentEventId)!!.root
            newBobSession.cryptoService().requestRoomKeyForEvent(event)
        }

        
        testHelper.runBlockingTest {
            delay(10_000)
        }

        
        ensureCannotDecrypt(sentEventIds, newBobSession, e2eRoomID, MXCryptoError.ErrorType.KEYS_WITHHELD)

        

        bobSession.cryptoService().verificationService().markedLocallyAsManuallyVerified(newBobSession.myUserId, newBobSession.sessionParams.deviceId!!)
        newBobSession.cryptoService().verificationService().markedLocallyAsManuallyVerified(bobSession.myUserId, bobSession.sessionParams.deviceId!!)

        
        sentEventIds.forEach { sentEventId ->
            val event = newBobSession.getRoom(e2eRoomID)!!.getTimelineEvent(sentEventId)!!.root
            newBobSession.cryptoService().reRequestRoomKeyForEvent(event)
        }

        
        testHelper.runBlockingTest {
            delay(10_000)
        }

        ensureCanDecrypt(sentEventIds, newBobSession, e2eRoomID, messagesText)

        cryptoTestData.cleanUp(testHelper)
        testHelper.signOutAndClose(newBobSession)
    }

    
    @Test
    fun testForwardBetterKey() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoom(true)
        val aliceSession = cryptoTestData.firstSession
        val bobSessionWithBetterKey = cryptoTestData.secondSession!!
        val e2eRoomID = cryptoTestData.roomId

        val aliceRoomPOV = aliceSession.getRoom(e2eRoomID)!!

        cryptoTestHelper.initializeCrossSigning(bobSessionWithBetterKey)

        
        var firstEventId: String
        val firstMessage = "1. Hello"

        Log.v("#E2E TEST", "Alice sends some messages")
        firstMessage.let { text ->
            firstEventId = sendMessageInRoom(aliceRoomPOV, text)!!

            testHelper.waitWithLatch { latch ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val timelineEvent = bobSessionWithBetterKey.getRoom(e2eRoomID)?.getTimelineEvent(firstEventId)
                    timelineEvent != null &&
                            timelineEvent.isEncrypted() &&
                            timelineEvent.root.getClearType() == EventType.MESSAGE
                }
            }
        }

        
        ensureIsDecrypted(listOf(firstEventId), bobSessionWithBetterKey, e2eRoomID)

        
        val newBobSession = testHelper.logIntoAccount(bobSessionWithBetterKey.myUserId, SessionTestParams(true))

        
        Log.v("#E2E TEST", "check that new bob can't currently decrypt")
        ensureCannotDecrypt(listOf(firstEventId), newBobSession, e2eRoomID, MXCryptoError.ErrorType.UNKNOWN_INBOUND_SESSION_ID)

        
        var secondEventId: String
        val secondMessage = "2. New Device?"

        Log.v("#E2E TEST", "Alice sends some messages")
        secondMessage.let { text ->
            secondEventId = sendMessageInRoom(aliceRoomPOV, text)!!

            testHelper.waitWithLatch { latch ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val timelineEvent = newBobSession.getRoom(e2eRoomID)?.getTimelineEvent(secondEventId)
                    timelineEvent != null &&
                            timelineEvent.isEncrypted() &&
                            timelineEvent.root.getClearType() == EventType.MESSAGE
                }
            }
        }

        
        val firstEventNewBobPov = newBobSession.getRoom(e2eRoomID)?.getTimelineEvent(firstEventId)
        val secondEventNewBobPov = newBobSession.getRoom(e2eRoomID)?.getTimelineEvent(secondEventId)

        val firstSessionId = firstEventNewBobPov!!.root.content.toModel<EncryptedEventContent>()!!.sessionId!!
        val secondSessionId = secondEventNewBobPov!!.root.content.toModel<EncryptedEventContent>()!!.sessionId!!

        Assert.assertTrue("Should be the same session id", firstSessionId == secondSessionId)

        
        testHelper.runBlockingTest {
            try {
                newBobSession.cryptoService().decryptEvent(firstEventNewBobPov.root, "")
                fail("Should not be able to decrypt event")
            } catch (error: MXCryptoError) {
                val errorType = (error as? MXCryptoError.Base)?.errorType
                assertEquals(MXCryptoError.ErrorType.UNKNOWN_MESSAGE_INDEX, errorType)
            }
        }

        testHelper.runBlockingTest {
            try {
                newBobSession.cryptoService().decryptEvent(secondEventNewBobPov.root, "")
            } catch (error: MXCryptoError) {
                fail("Should be able to decrypt event")
            }
        }

        
        bobSessionWithBetterKey.cryptoService()
                .verificationService()
                .markedLocallyAsManuallyVerified(newBobSession.myUserId, newBobSession.sessionParams.deviceId!!)

        newBobSession.cryptoService()
                .verificationService()
                .markedLocallyAsManuallyVerified(bobSessionWithBetterKey.myUserId, bobSessionWithBetterKey.sessionParams.deviceId!!)

        
        newBobSession.cryptoService().requestRoomKeyForEvent(firstEventNewBobPov.root)

        
        testHelper.runBlockingTest {
            delay(10_000)
        }

        
        
        testHelper.runBlockingTest {
            try {
                newBobSession.cryptoService().decryptEvent(firstEventNewBobPov.root, "")
            } catch (error: MXCryptoError) {
                fail("Should be able to decrypt first event now $error")
            }
        }
        testHelper.runBlockingTest {
            try {
                newBobSession.cryptoService().decryptEvent(secondEventNewBobPov.root, "")
            } catch (error: MXCryptoError) {
                fail("Should be able to decrypt event $error")
            }
        }

        cryptoTestData.cleanUp(testHelper)
        testHelper.signOutAndClose(newBobSession)
    }

    private fun sendMessageInRoom(aliceRoomPOV: Room, text: String): String? {
        aliceRoomPOV.sendTextMessage(text)
        var sentEventId: String? = null
        testHelper.waitWithLatch(4 * TestConstants.timeOutMillis) { latch ->
            val timeline = aliceRoomPOV.createTimeline(null, TimelineSettings(60))
            timeline.start()

            testHelper.retryPeriodicallyWithLatch(latch) {
                val decryptedMsg = timeline.getSnapshot()
                        .filter { it.root.getClearType() == EventType.MESSAGE }
                        .also { list ->
                            val message = list.joinToString(",", "[", "]") { "${it.root.type}|${it.root.sendState}" }
                            Log.v("#E2E TEST", "Timeline snapshot is $message")
                        }
                        .filter { it.root.sendState == SendState.SYNCED }
                        .firstOrNull { it.root.getClearContent().toModel<MessageContent>()?.body?.startsWith(text) == true }
                sentEventId = decryptedMsg?.eventId
                decryptedMsg != null
            }

            timeline.dispose()
        }
        return sentEventId
    }

    private fun ensureMembersHaveJoined(aliceSession: Session, otherAccounts: List<Session>, e2eRoomID: String) {
        testHelper.waitWithLatch { latch ->
            testHelper.retryPeriodicallyWithLatch(latch) {
                otherAccounts.map {
                    aliceSession.getRoomMember(it.myUserId, e2eRoomID)?.membership
                }.all {
                    it == Membership.JOIN
                }
            }
        }
    }

    private fun waitForAndAcceptInviteInRoom(otherSession: Session, e2eRoomID: String) {
        testHelper.waitWithLatch { latch ->
            testHelper.retryPeriodicallyWithLatch(latch) {
                val roomSummary = otherSession.getRoomSummary(e2eRoomID)
                (roomSummary != null && roomSummary.membership == Membership.INVITE).also {
                    if (it) {
                        Log.v("#E2E TEST", "${otherSession.myUserId} can see the invite from alice")
                    }
                }
            }
        }

        testHelper.runBlockingTest(60_000) {
            Log.v("#E2E TEST", "${otherSession.myUserId} tries to join room $e2eRoomID")
            try {
                otherSession.joinRoom(e2eRoomID)
            } catch (ex: JoinRoomFailure.JoinedWithTimeout) {
                
            }
        }

        Log.v("#E2E TEST", "${otherSession.myUserId} waiting for join echo ...")
        testHelper.waitWithLatch {
            testHelper.retryPeriodicallyWithLatch(it) {
                val roomSummary = otherSession.getRoomSummary(e2eRoomID)
                roomSummary != null && roomSummary.membership == Membership.JOIN
            }
        }
    }

    private fun ensureCanDecrypt(sentEventIds: MutableList<String>, session: Session, e2eRoomID: String, messagesText: List<String>) {
        sentEventIds.forEachIndexed { index, sentEventId ->
            testHelper.waitWithLatch { latch ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val event = session.getRoom(e2eRoomID)!!.getTimelineEvent(sentEventId)!!.root
                    testHelper.runBlockingTest {
                        try {
                            session.cryptoService().decryptEvent(event, "").let { result ->
                                event.mxDecryptionResult = OlmDecryptionResult(
                                        payload = result.clearEvent,
                                        senderKey = result.senderCurve25519Key,
                                        keysClaimed = result.claimedEd25519Key?.let { mapOf("ed25519" to it) },
                                        forwardingCurve25519KeyChain = result.forwardingCurve25519KeyChain
                                )
                            }
                        } catch (error: MXCryptoError) {
                            
                        }
                    }
                    event.getClearType() == EventType.MESSAGE &&
                            messagesText[index] == event.getClearContent()?.toModel<MessageContent>()?.body
                }
            }
        }
    }

    private fun ensureIsDecrypted(sentEventIds: List<String>, session: Session, e2eRoomID: String) {
        testHelper.waitWithLatch { latch ->
            sentEventIds.forEach { sentEventId ->
                testHelper.retryPeriodicallyWithLatch(latch) {
                    val timelineEvent = session.getRoom(e2eRoomID)?.getTimelineEvent(sentEventId)
                    timelineEvent != null &&
                            timelineEvent.isEncrypted() &&
                            timelineEvent.root.getClearType() == EventType.MESSAGE
                }
            }
        }
    }

    private fun ensureCannotDecrypt(sentEventIds: List<String>, newBobSession: Session, e2eRoomID: String, expectedError: MXCryptoError.ErrorType?) {
        sentEventIds.forEach { sentEventId ->
            val event = newBobSession.getRoom(e2eRoomID)!!.getTimelineEvent(sentEventId)!!.root
            testHelper.runBlockingTest {
                try {
                    newBobSession.cryptoService().decryptEvent(event, "")
                    fail("Should not be able to decrypt event")
                } catch (error: MXCryptoError) {
                    val errorType = (error as? MXCryptoError.Base)?.errorType
                    if (expectedError == null) {
                        Assert.assertNotNull(errorType)
                    } else {
                        assertEquals(expectedError, errorType, "Message expected to be UISI")
                    }
                }
            }
        }
    }
}
