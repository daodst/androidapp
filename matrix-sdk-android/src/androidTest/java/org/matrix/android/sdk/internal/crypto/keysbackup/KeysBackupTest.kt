

package org.matrix.android.sdk.internal.crypto.keysbackup

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM_BACKUP
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.listeners.StepProgressListener
import org.matrix.android.sdk.api.session.crypto.crosssigning.DeviceTrustLevel
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupLastVersionResult
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupStateListener
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupVersionTrust
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersion
import org.matrix.android.sdk.api.session.crypto.keysbackup.MegolmBackupCreationInfo
import org.matrix.android.sdk.api.session.crypto.keysbackup.toKeysVersionResult
import org.matrix.android.sdk.api.session.crypto.model.ImportRoomKeysResult
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.CryptoTestHelper
import org.matrix.android.sdk.common.TestConstants
import org.matrix.android.sdk.common.TestMatrixCallback
import java.util.Collections
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
@LargeTest
class KeysBackupTest : InstrumentedTest {

    private val testHelper = CommonTestHelper(context())
    private val cryptoTestHelper = CryptoTestHelper(testHelper)
    private val keysBackupTestHelper = KeysBackupTestHelper(testHelper, cryptoTestHelper)

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun roomKeysTest_testBackupStore_ok() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        
        val cryptoStore = (cryptoTestData.firstSession.cryptoService().keysBackupService() as DefaultKeysBackupService).store
        val sessions = cryptoStore.inboundGroupSessionsToBackup(100)
        val sessionsCount = sessions.size

        assertFalse(sessions.isEmpty())
        assertEquals(sessionsCount, cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(false))
        assertEquals(0, cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(true))

        
        val session = sessions[0]

        cryptoStore.markBackupDoneForInboundGroupSessions(Collections.singletonList(session))

        assertEquals(sessionsCount, cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(false))
        assertEquals(1, cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(true))

        val sessions2 = cryptoStore.inboundGroupSessionsToBackup(100)
        assertEquals(sessionsCount - 1, sessions2.size)

        
        cryptoStore.resetBackupMarkers()

        val sessions3 = cryptoStore.inboundGroupSessionsToBackup(100)
        assertEquals(sessionsCount, sessions3.size)
        assertEquals(sessionsCount, cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(false))
        assertEquals(0, cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(true))

        cryptoTestData.cleanUp(testHelper)
    }

    
    @Test
    fun prepareKeysBackupVersionTest() {
        val bobSession = testHelper.createAccount(TestConstants.USER_BOB, KeysBackupTestConstants.defaultSessionParams)

        assertNotNull(bobSession.cryptoService().keysBackupService())

        val keysBackup = bobSession.cryptoService().keysBackupService()

        val stateObserver = StateObserver(keysBackup)

        assertFalse(keysBackup.isEnabled)

        val megolmBackupCreationInfo = testHelper.doSync<MegolmBackupCreationInfo> {
            keysBackup.prepareKeysBackupVersion(null, null, it)
        }

        assertEquals(MXCRYPTO_ALGORITHM_MEGOLM_BACKUP, megolmBackupCreationInfo.algorithm)
        assertNotNull(megolmBackupCreationInfo.authData.publicKey)
        assertNotNull(megolmBackupCreationInfo.authData.signatures)
        assertNotNull(megolmBackupCreationInfo.recoveryKey)

        stateObserver.stopAndCheckStates(null)
        testHelper.signOutAndClose(bobSession)
    }

    
    @Test
    fun createKeysBackupVersionTest() {
        val bobSession = testHelper.createAccount(TestConstants.USER_BOB, KeysBackupTestConstants.defaultSessionParams)

        val keysBackup = bobSession.cryptoService().keysBackupService()

        val stateObserver = StateObserver(keysBackup)

        assertFalse(keysBackup.isEnabled)

        val megolmBackupCreationInfo = testHelper.doSync<MegolmBackupCreationInfo> {
            keysBackup.prepareKeysBackupVersion(null, null, it)
        }

        assertFalse(keysBackup.isEnabled)

        
        testHelper.doSync<KeysVersion> {
            keysBackup.createKeysBackupVersion(megolmBackupCreationInfo, it)
        }

        
        assertTrue(keysBackup.isEnabled)

        stateObserver.stopAndCheckStates(null)
        testHelper.signOutAndClose(bobSession)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun backupAfterCreateKeysBackupVersionTest() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        keysBackupTestHelper.waitForKeybackUpBatching()
        val keysBackup = cryptoTestData.firstSession.cryptoService().keysBackupService()

        val latch = CountDownLatch(1)

        assertEquals(2, cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(false))
        assertEquals(0, cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(true))

        val stateObserver = StateObserver(keysBackup, latch, 5)

        keysBackupTestHelper.prepareAndCreateKeysBackupData(keysBackup)

        testHelper.await(latch)

        val nbOfKeys = cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(false)
        val backedUpKeys = cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(true)

        assertEquals(2, nbOfKeys)
        assertEquals("All keys must have been marked as backed up", nbOfKeys, backedUpKeys)

        
        stateObserver.stopAndCheckStates(
                listOf(
                        KeysBackupState.Enabling,
                        KeysBackupState.ReadyToBackUp,
                        KeysBackupState.WillBackUp,
                        KeysBackupState.BackingUp,
                        KeysBackupState.ReadyToBackUp
                )
        )
        cryptoTestData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun backupAllGroupSessionsTest() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        val keysBackup = cryptoTestData.firstSession.cryptoService().keysBackupService()

        val stateObserver = StateObserver(keysBackup)

        keysBackupTestHelper.prepareAndCreateKeysBackupData(keysBackup)

        
        val nbOfKeys = cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(false)

        assertEquals(2, nbOfKeys)

        var lastBackedUpKeysProgress = 0

        testHelper.doSync<Unit> {
            keysBackup.backupAllGroupSessions(object : ProgressListener {
                override fun onProgress(progress: Int, total: Int) {
                    assertEquals(nbOfKeys, total)
                    lastBackedUpKeysProgress = progress
                }
            }, it)
        }

        assertEquals(nbOfKeys, lastBackedUpKeysProgress)

        val backedUpKeys = cryptoTestData.firstSession.cryptoService().inboundGroupSessionsCount(true)

        assertEquals("All keys must have been marked as backed up", nbOfKeys, backedUpKeys)

        stateObserver.stopAndCheckStates(null)
        cryptoTestData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun testEncryptAndDecryptKeysBackupData() {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        val keysBackup = cryptoTestData.firstSession.cryptoService().keysBackupService() as DefaultKeysBackupService

        val stateObserver = StateObserver(keysBackup)

        
        val session = keysBackup.store.inboundGroupSessionsToBackup(1)[0]

        val keyBackupCreationInfo = keysBackupTestHelper.prepareAndCreateKeysBackupData(keysBackup).megolmBackupCreationInfo

        
        val keyBackupData = keysBackup.encryptGroupSession(session)
        assertNotNull(keyBackupData)
        assertNotNull(keyBackupData!!.sessionData)

        
        val decryption = keysBackup.pkDecryptionFromRecoveryKey(keyBackupCreationInfo.recoveryKey)
        assertNotNull(decryption)
        
        val sessionData = keysBackup
                .decryptKeyBackupData(keyBackupData,
                        session.olmInboundGroupSession!!.sessionIdentifier(),
                        cryptoTestData.roomId,
                        decryption!!)
        assertNotNull(sessionData)
        
        keysBackupTestHelper.assertKeysEquals(session.exportKeys(), sessionData)

        stateObserver.stopAndCheckStates(null)
        cryptoTestData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun restoreKeysBackupTest() {
        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(null)

        
        val importRoomKeysResult = testHelper.doSync<ImportRoomKeysResult> {
            testData.aliceSession2.cryptoService().keysBackupService().restoreKeysWithRecoveryKey(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                    testData.prepareKeysBackupDataResult.megolmBackupCreationInfo.recoveryKey,
                    null,
                    null,
                    null,
                    it
            )
        }

        keysBackupTestHelper.checkRestoreSuccess(testData, importRoomKeysResult.totalNumberOfKeys, importRoomKeysResult.successfullyNumberOfImportedKeys)

        testData.cleanUp(testHelper)
    }

    









    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun trustKeyBackupVersionTest() {
        
        
        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(null)

        val stateObserver = StateObserver(testData.aliceSession2.cryptoService().keysBackupService())

        
        assertNotNull(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion)
        assertFalse(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)
        assertEquals(KeysBackupState.NotTrusted, testData.aliceSession2.cryptoService().keysBackupService().state)

        
        testHelper.doSync<Unit> {
            testData.aliceSession2.cryptoService().keysBackupService().trustKeysBackupVersion(
                    testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                    true,
                    it
            )
        }

        
        keysBackupTestHelper.waitForKeysBackupToBeInState(testData.aliceSession2, KeysBackupState.ReadyToBackUp)

        
        assertEquals(testData.prepareKeysBackupDataResult.version, testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion?.version)
        assertTrue(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)

        
        val keysVersionResult = testHelper.doSync<KeysBackupLastVersionResult> {
            testData.aliceSession2.cryptoService().keysBackupService().getCurrentVersion(it)
        }.toKeysVersionResult()

        
        assertEquals(testData.prepareKeysBackupDataResult.version, keysVersionResult!!.version)

        val keysBackupVersionTrust = testHelper.doSync<KeysBackupVersionTrust> {
            testData.aliceSession2.cryptoService().keysBackupService().getKeysBackupTrust(keysVersionResult, it)
        }

        
        assertTrue(keysBackupVersionTrust.usable)
        assertEquals(2, keysBackupVersionTrust.signatures.size)

        stateObserver.stopAndCheckStates(null)
        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun trustKeyBackupVersionWithRecoveryKeyTest() {
        
        
        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(null)

        val stateObserver = StateObserver(testData.aliceSession2.cryptoService().keysBackupService())

        
        assertNotNull(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion)
        assertFalse(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)
        assertEquals(KeysBackupState.NotTrusted, testData.aliceSession2.cryptoService().keysBackupService().state)

        
        testHelper.doSync<Unit> {
            testData.aliceSession2.cryptoService().keysBackupService().trustKeysBackupVersionWithRecoveryKey(
                    testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                    testData.prepareKeysBackupDataResult.megolmBackupCreationInfo.recoveryKey,
                    it
            )
        }

        
        keysBackupTestHelper.waitForKeysBackupToBeInState(testData.aliceSession2, KeysBackupState.ReadyToBackUp)

        
        assertEquals(testData.prepareKeysBackupDataResult.version, testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion?.version)
        assertTrue(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)

        
        val keysVersionResult = testHelper.doSync<KeysBackupLastVersionResult> {
            testData.aliceSession2.cryptoService().keysBackupService().getCurrentVersion(it)
        }.toKeysVersionResult()

        
        assertEquals(testData.prepareKeysBackupDataResult.version, keysVersionResult!!.version)

        val keysBackupVersionTrust = testHelper.doSync<KeysBackupVersionTrust> {
            testData.aliceSession2.cryptoService().keysBackupService().getKeysBackupTrust(keysVersionResult, it)
        }

        
        assertTrue(keysBackupVersionTrust.usable)
        assertEquals(2, keysBackupVersionTrust.signatures.size)

        stateObserver.stopAndCheckStates(null)
        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun trustKeyBackupVersionWithWrongRecoveryKeyTest() {
        
        
        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(null)

        val stateObserver = StateObserver(testData.aliceSession2.cryptoService().keysBackupService())

        
        assertNotNull(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion)
        assertFalse(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)
        assertEquals(KeysBackupState.NotTrusted, testData.aliceSession2.cryptoService().keysBackupService().state)

        
        val latch = CountDownLatch(1)
        testData.aliceSession2.cryptoService().keysBackupService().trustKeysBackupVersionWithRecoveryKey(
                testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                "Bad recovery key",
                TestMatrixCallback(latch, false)
        )
        testHelper.await(latch)

        
        assertNotNull(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion)
        assertFalse(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)
        assertEquals(KeysBackupState.NotTrusted, testData.aliceSession2.cryptoService().keysBackupService().state)

        stateObserver.stopAndCheckStates(null)
        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun trustKeyBackupVersionWithPasswordTest() {
        val password = "Password"

        
        
        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(password)

        val stateObserver = StateObserver(testData.aliceSession2.cryptoService().keysBackupService())

        
        assertNotNull(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion)
        assertFalse(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)
        assertEquals(KeysBackupState.NotTrusted, testData.aliceSession2.cryptoService().keysBackupService().state)

        
        testHelper.doSync<Unit> {
            testData.aliceSession2.cryptoService().keysBackupService().trustKeysBackupVersionWithPassphrase(
                    testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                    password,
                    it
            )
        }

        
        keysBackupTestHelper.waitForKeysBackupToBeInState(testData.aliceSession2, KeysBackupState.ReadyToBackUp)

        
        assertEquals(testData.prepareKeysBackupDataResult.version, testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion?.version)
        assertTrue(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)

        
        val keysVersionResult = testHelper.doSync<KeysBackupLastVersionResult> {
            testData.aliceSession2.cryptoService().keysBackupService().getCurrentVersion(it)
        }.toKeysVersionResult()

        
        assertEquals(testData.prepareKeysBackupDataResult.version, keysVersionResult!!.version)

        val keysBackupVersionTrust = testHelper.doSync<KeysBackupVersionTrust> {
            testData.aliceSession2.cryptoService().keysBackupService().getKeysBackupTrust(keysVersionResult, it)
        }

        
        assertTrue(keysBackupVersionTrust.usable)
        assertEquals(2, keysBackupVersionTrust.signatures.size)

        stateObserver.stopAndCheckStates(null)
        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun trustKeyBackupVersionWithWrongPasswordTest() {
        val password = "Password"
        val badPassword = "Bad Password"

        
        
        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(password)

        val stateObserver = StateObserver(testData.aliceSession2.cryptoService().keysBackupService())

        
        assertNotNull(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion)
        assertFalse(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)
        assertEquals(KeysBackupState.NotTrusted, testData.aliceSession2.cryptoService().keysBackupService().state)

        
        val latch = CountDownLatch(1)
        testData.aliceSession2.cryptoService().keysBackupService().trustKeysBackupVersionWithPassphrase(
                testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                badPassword,
                TestMatrixCallback(latch, false)
        )
        testHelper.await(latch)

        
        assertNotNull(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion)
        assertFalse(testData.aliceSession2.cryptoService().keysBackupService().isEnabled)
        assertEquals(KeysBackupState.NotTrusted, testData.aliceSession2.cryptoService().keysBackupService().state)

        stateObserver.stopAndCheckStates(null)
        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun restoreKeysBackupWithAWrongRecoveryKeyTest() {
        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(null)

        
        val latch2 = CountDownLatch(1)
        var importRoomKeysResult: ImportRoomKeysResult? = null
        testData.aliceSession2.cryptoService().keysBackupService().restoreKeysWithRecoveryKey(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                "EsTc LW2K PGiF wKEA 3As5 g5c4 BXwk qeeJ ZJV8 Q9fu gUMN UE4d",
                null,
                null,
                null,
                object : TestMatrixCallback<ImportRoomKeysResult>(latch2, false) {
                    override fun onSuccess(data: ImportRoomKeysResult) {
                        importRoomKeysResult = data
                        super.onSuccess(data)
                    }
                }
        )
        testHelper.await(latch2)

        
        assertNull(importRoomKeysResult)

        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun testBackupWithPassword() {
        val password = "password"

        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(password)

        
        val steps = ArrayList<StepProgressListener.Step>()

        val importRoomKeysResult = testHelper.doSync<ImportRoomKeysResult> {
            testData.aliceSession2.cryptoService().keysBackupService().restoreKeyBackupWithPassword(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                    password,
                    null,
                    null,
                    object : StepProgressListener {
                        override fun onStepProgress(step: StepProgressListener.Step) {
                            steps.add(step)
                        }
                    },
                    it
            )
        }

        
        assertEquals(105, steps.size)

        for (i in 0..100) {
            assertTrue(steps[i] is StepProgressListener.Step.ComputingKey)
            assertEquals(i, (steps[i] as StepProgressListener.Step.ComputingKey).progress)
            assertEquals(100, (steps[i] as StepProgressListener.Step.ComputingKey).total)
        }

        assertTrue(steps[101] is StepProgressListener.Step.DownloadingKey)

        
        for (i in 102..104) {
            assertTrue(steps[i] is StepProgressListener.Step.ImportingKey)
            assertEquals(100, (steps[i] as StepProgressListener.Step.ImportingKey).total)
        }

        assertEquals(0, (steps[102] as StepProgressListener.Step.ImportingKey).progress)
        assertEquals(50, (steps[103] as StepProgressListener.Step.ImportingKey).progress)
        assertEquals(100, (steps[104] as StepProgressListener.Step.ImportingKey).progress)

        keysBackupTestHelper.checkRestoreSuccess(testData, importRoomKeysResult.totalNumberOfKeys, importRoomKeysResult.successfullyNumberOfImportedKeys)

        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun restoreKeysBackupWithAWrongPasswordTest() {
        val password = "password"
        val wrongPassword = "passw0rd"

        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(password)

        
        val latch2 = CountDownLatch(1)
        var importRoomKeysResult: ImportRoomKeysResult? = null
        testData.aliceSession2.cryptoService().keysBackupService().restoreKeyBackupWithPassword(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                wrongPassword,
                null,
                null,
                null,
                object : TestMatrixCallback<ImportRoomKeysResult>(latch2, false) {
                    override fun onSuccess(data: ImportRoomKeysResult) {
                        importRoomKeysResult = data
                        super.onSuccess(data)
                    }
                }
        )
        testHelper.await(latch2)

        
        assertNull(importRoomKeysResult)

        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun testUseRecoveryKeyToRestoreAPasswordBasedKeysBackup() {
        val password = "password"

        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(password)

        
        val importRoomKeysResult = testHelper.doSync<ImportRoomKeysResult> {
            testData.aliceSession2.cryptoService().keysBackupService().restoreKeysWithRecoveryKey(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                    testData.prepareKeysBackupDataResult.megolmBackupCreationInfo.recoveryKey,
                    null,
                    null,
                    null,
                    it
            )
        }

        keysBackupTestHelper.checkRestoreSuccess(testData, importRoomKeysResult.totalNumberOfKeys, importRoomKeysResult.successfullyNumberOfImportedKeys)

        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun testUsePasswordToRestoreARecoveryKeyBasedKeysBackup() {
        val testData = keysBackupTestHelper.createKeysBackupScenarioWithPassword(null)

        
        val latch2 = CountDownLatch(1)
        var importRoomKeysResult: ImportRoomKeysResult? = null
        testData.aliceSession2.cryptoService().keysBackupService().restoreKeyBackupWithPassword(testData.aliceSession2.cryptoService().keysBackupService().keysBackupVersion!!,
                "password",
                null,
                null,
                null,
                object : TestMatrixCallback<ImportRoomKeysResult>(latch2, false) {
                    override fun onSuccess(data: ImportRoomKeysResult) {
                        importRoomKeysResult = data
                        super.onSuccess(data)
                    }
                }
        )
        testHelper.await(latch2)

        
        assertNull(importRoomKeysResult)

        testData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun testIsKeysBackupTrusted() {
        
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        val keysBackup = cryptoTestData.firstSession.cryptoService().keysBackupService()

        val stateObserver = StateObserver(keysBackup)

        
        keysBackupTestHelper.prepareAndCreateKeysBackupData(keysBackup)

        
        val keysVersionResult = testHelper.doSync<KeysBackupLastVersionResult> {
            keysBackup.getCurrentVersion(it)
        }.toKeysVersionResult()

        
        val keysBackupVersionTrust = testHelper.doSync<KeysBackupVersionTrust> {
            keysBackup.getKeysBackupTrust(keysVersionResult!!, it)
        }

        assertNotNull(keysBackupVersionTrust)
        assertTrue(keysBackupVersionTrust.usable)
        assertEquals(1, keysBackupVersionTrust.signatures.size)

        val signature = keysBackupVersionTrust.signatures[0]
        assertTrue(signature.valid)
        assertNotNull(signature.device)
        assertEquals(cryptoTestData.firstSession.cryptoService().getMyDevice().deviceId, signature.deviceId)
        assertEquals(signature.device!!.deviceId, cryptoTestData.firstSession.sessionParams.deviceId)

        stateObserver.stopAndCheckStates(null)
        cryptoTestData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun testCheckAndStartKeysBackupWhenRestartingAMatrixSession() {
        
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        val keysBackup = cryptoTestData.firstSession.cryptoService().keysBackupService()

        val stateObserver = StateObserver(keysBackup)

        assertFalse(keysBackup.isEnabled)

        val keyBackupCreationInfo = keysBackupTestHelper.prepareAndCreateKeysBackupData(keysBackup)

        assertTrue(keysBackup.isEnabled)

        
        
        val aliceSession2 = testHelper.logIntoAccount(cryptoTestData.firstSession.myUserId, KeysBackupTestConstants.defaultSessionParamsWithInitialSync)

        cryptoTestData.cleanUp(testHelper)

        val keysBackup2 = aliceSession2.cryptoService().keysBackupService()

        val stateObserver2 = StateObserver(keysBackup2)

        
        val latch = CountDownLatch(1)
        var count = 0
        keysBackup2.addListener(object : KeysBackupStateListener {
            override fun onStateChange(newState: KeysBackupState) {
                
                if (newState == KeysBackupState.ReadyToBackUp) {
                    count++

                    if (count == 2) {
                        
                        keysBackup2.removeListener(this)

                        latch.countDown()
                    }
                }
            }
        })
        testHelper.await(latch)

        assertEquals(keyBackupCreationInfo.version, keysBackup2.currentBackupVersion)

        stateObserver.stopAndCheckStates(null)
        stateObserver2.stopAndCheckStates(null)
        testHelper.signOutAndClose(aliceSession2)
    }

    
    @Test
    fun testBackupWhenAnotherBackupWasCreated() {
        
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        val keysBackup = cryptoTestData.firstSession.cryptoService().keysBackupService()

        val stateObserver = StateObserver(keysBackup)

        assertFalse(keysBackup.isEnabled)

        
        val latch0 = CountDownLatch(1)
        var count = 0
        keysBackup.addListener(object : KeysBackupStateListener {
            override fun onStateChange(newState: KeysBackupState) {
                
                if (newState == KeysBackupState.ReadyToBackUp) {
                    count++

                    if (count == 2) {
                        
                        keysBackup.removeListener(this)

                        latch0.countDown()
                    }
                }
            }
        })

        
        keysBackupTestHelper.prepareAndCreateKeysBackupData(keysBackup)

        assertTrue(keysBackup.isEnabled)

        testHelper.await(latch0)

        
        val megolmBackupCreationInfo = cryptoTestHelper.createFakeMegolmBackupCreationInfo()
        testHelper.doSync<KeysVersion> {
            (keysBackup as DefaultKeysBackupService).createFakeKeysBackupVersion(megolmBackupCreationInfo, it)
        }

        
        (cryptoTestData.firstSession.cryptoService().keysBackupService() as DefaultKeysBackupService).store.resetBackupMarkers()

        
        val latch2 = CountDownLatch(1)
        keysBackup.backupAllGroupSessions(null, TestMatrixCallback(latch2, false))
        testHelper.await(latch2)

        
        assertEquals(KeysBackupState.WrongBackUpVersion, keysBackup.state)
        assertFalse(keysBackup.isEnabled)

        stateObserver.stopAndCheckStates(null)
        cryptoTestData.cleanUp(testHelper)
    }

    
    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun testBackupAfterVerifyingADevice() {
        
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        val keysBackup = cryptoTestData.firstSession.cryptoService().keysBackupService()

        val stateObserver = StateObserver(keysBackup)

        
        keysBackupTestHelper.prepareAndCreateKeysBackupData(keysBackup)

        
        testHelper.doSync<Unit> {
            keysBackup.backupAllGroupSessions(null, it)
        }

        val oldDeviceId = cryptoTestData.firstSession.sessionParams.deviceId!!
        val oldKeyBackupVersion = keysBackup.currentBackupVersion
        val aliceUserId = cryptoTestData.firstSession.myUserId

        
        val aliceSession2 = testHelper.logIntoAccount(aliceUserId, KeysBackupTestConstants.defaultSessionParamsWithInitialSync)

        
        aliceSession2.cryptoService().setWarnOnUnknownDevices(false)

        val room2 = aliceSession2.getRoom(cryptoTestData.roomId)!!

        testHelper.sendTextMessage(room2, "New key", 1)

        
        val keysBackup2 = aliceSession2.cryptoService().keysBackupService()

        val stateObserver2 = StateObserver(keysBackup2)

        var isSuccessful = false
        val latch2 = CountDownLatch(1)
        keysBackup2.backupAllGroupSessions(
                null,
                object : TestMatrixCallback<Unit>(latch2, false) {
                    override fun onSuccess(data: Unit) {
                        isSuccessful = true
                        super.onSuccess(data)
                    }
                })
        testHelper.await(latch2)

        assertFalse(isSuccessful)

        
        assertEquals(KeysBackupState.NotTrusted, keysBackup2.state)
        assertFalse(keysBackup2.isEnabled)

        
        aliceSession2.cryptoService().setDeviceVerification(DeviceTrustLevel(crossSigningVerified = false, locallyVerified = true), aliceSession2.myUserId, oldDeviceId)

        
        val latch4 = CountDownLatch(1)
        keysBackup2.addListener(object : KeysBackupStateListener {
            override fun onStateChange(newState: KeysBackupState) {
                
                if (keysBackup2.state == KeysBackupState.ReadyToBackUp) {
                    
                    keysBackup2.removeListener(this)

                    latch4.countDown()
                }
            }
        })
        testHelper.await(latch4)

        
        assertEquals(oldKeyBackupVersion, aliceSession2.cryptoService().keysBackupService().currentBackupVersion)

        testHelper.doSync<Unit> {
            aliceSession2.cryptoService().keysBackupService().backupAllGroupSessions(null, it)
        }

        
        assertTrue(aliceSession2.cryptoService().keysBackupService().isEnabled)

        stateObserver.stopAndCheckStates(null)
        stateObserver2.stopAndCheckStates(null)
        testHelper.signOutAndClose(aliceSession2)
        cryptoTestData.cleanUp(testHelper)
    }

    
    @Test
    fun deleteKeysBackupTest() {
        
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        val keysBackup = cryptoTestData.firstSession.cryptoService().keysBackupService()

        val stateObserver = StateObserver(keysBackup)

        assertFalse(keysBackup.isEnabled)

        val keyBackupCreationInfo = keysBackupTestHelper.prepareAndCreateKeysBackupData(keysBackup)

        assertTrue(keysBackup.isEnabled)

        
        testHelper.doSync<Unit> { keysBackup.deleteBackup(keyBackupCreationInfo.version, it) }

        
        assertFalse(keysBackup.isEnabled)

        stateObserver.stopAndCheckStates(null)
        cryptoTestData.cleanUp(testHelper)
    }
}
