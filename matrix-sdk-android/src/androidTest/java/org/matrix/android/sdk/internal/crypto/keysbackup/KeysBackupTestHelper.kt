

package org.matrix.android.sdk.internal.crypto.keysbackup

import org.junit.Assert
import org.matrix.android.sdk.api.listeners.ProgressListener
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupService
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupState
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysBackupStateListener
import org.matrix.android.sdk.api.session.crypto.keysbackup.KeysVersion
import org.matrix.android.sdk.api.session.crypto.keysbackup.MegolmBackupCreationInfo
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.CryptoTestHelper
import org.matrix.android.sdk.common.assertDictEquals
import org.matrix.android.sdk.common.assertListEquals
import org.matrix.android.sdk.internal.crypto.MegolmSessionData
import java.util.concurrent.CountDownLatch

internal class KeysBackupTestHelper(
        private val testHelper: CommonTestHelper,
        private val cryptoTestHelper: CryptoTestHelper) {

    fun waitForKeybackUpBatching() {
        Thread.sleep(400)
    }

    
    fun createKeysBackupScenarioWithPassword(password: String?): KeysBackupScenarioData {
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoomWithEncryptedMessages()

        waitForKeybackUpBatching()

        val cryptoStore = (cryptoTestData.firstSession.cryptoService().keysBackupService() as DefaultKeysBackupService).store
        val keysBackup = cryptoTestData.firstSession.cryptoService().keysBackupService()

        val stateObserver = StateObserver(keysBackup)

        val aliceKeys = cryptoStore.inboundGroupSessionsToBackup(100)

        
        val prepareKeysBackupDataResult = prepareAndCreateKeysBackupData(keysBackup, password)

        var lastProgress = 0
        var lastTotal = 0
        testHelper.doSync<Unit> {
            keysBackup.backupAllGroupSessions(object : ProgressListener {
                override fun onProgress(progress: Int, total: Int) {
                    lastProgress = progress
                    lastTotal = total
                }
            }, it)
        }

        Assert.assertEquals(2, lastProgress)
        Assert.assertEquals(2, lastTotal)

        val aliceUserId = cryptoTestData.firstSession.myUserId

        
        val aliceSession2 = testHelper.logIntoAccount(aliceUserId, KeysBackupTestConstants.defaultSessionParamsWithInitialSync)

        
        Assert.assertEquals(0, aliceSession2.cryptoService().inboundGroupSessionsCount(false))

        
        waitForKeysBackupToBeInState(aliceSession2, KeysBackupState.NotTrusted)

        stateObserver.stopAndCheckStates(null)

        return KeysBackupScenarioData(cryptoTestData,
                aliceKeys,
                prepareKeysBackupDataResult,
                aliceSession2)
    }

    fun prepareAndCreateKeysBackupData(keysBackup: KeysBackupService,
                                       password: String? = null): PrepareKeysBackupDataResult {
        val stateObserver = StateObserver(keysBackup)

        val megolmBackupCreationInfo = testHelper.doSync<MegolmBackupCreationInfo> {
            keysBackup.prepareKeysBackupVersion(password, null, it)
        }

        Assert.assertNotNull(megolmBackupCreationInfo)

        Assert.assertFalse(keysBackup.isEnabled)

        
        val keysVersion = testHelper.doSync<KeysVersion> {
            keysBackup.createKeysBackupVersion(megolmBackupCreationInfo, it)
        }

        Assert.assertNotNull(keysVersion.version)

        
        Assert.assertTrue(keysBackup.isEnabled)

        stateObserver.stopAndCheckStates(null)
        return PrepareKeysBackupDataResult(megolmBackupCreationInfo, keysVersion.version)
    }

    
    fun waitForKeysBackupToBeInState(session: Session, state: KeysBackupState) {
        
        if (session.cryptoService().keysBackupService().state == state) {
            return
        }

        
        val latch = CountDownLatch(1)

        session.cryptoService().keysBackupService().addListener(object : KeysBackupStateListener {
            override fun onStateChange(newState: KeysBackupState) {
                if (newState == state) {
                    session.cryptoService().keysBackupService().removeListener(this)
                    latch.countDown()
                }
            }
        })

        testHelper.await(latch)
    }

    fun assertKeysEquals(keys1: MegolmSessionData?, keys2: MegolmSessionData?) {
        Assert.assertNotNull(keys1)
        Assert.assertNotNull(keys2)

        Assert.assertEquals(keys1?.algorithm, keys2?.algorithm)
        Assert.assertEquals(keys1?.roomId, keys2?.roomId)
        
        
        Assert.assertEquals(keys1?.senderKey, keys2?.senderKey)
        Assert.assertEquals(keys1?.sessionId, keys2?.sessionId)
        Assert.assertEquals(keys1?.sessionKey, keys2?.sessionKey)

        assertListEquals(keys1?.forwardingCurve25519KeyChain, keys2?.forwardingCurve25519KeyChain)
        assertDictEquals(keys1?.senderClaimedKeys, keys2?.senderClaimedKeys)
    }

    
    fun checkRestoreSuccess(testData: KeysBackupScenarioData,
                            total: Int,
                            imported: Int) {
        
        Assert.assertEquals(testData.aliceKeys.size, total)
        Assert.assertEquals(total, imported)

        
        Assert.assertEquals(testData.aliceKeys.size, testData.aliceSession2.cryptoService().inboundGroupSessionsCount(false))

        
        for (aliceKey1 in testData.aliceKeys) {
            val aliceKey2 = (testData.aliceSession2.cryptoService().keysBackupService() as DefaultKeysBackupService).store
                    .getInboundGroupSession(aliceKey1.olmInboundGroupSession!!.sessionIdentifier(), aliceKey1.senderKey!!)
            Assert.assertNotNull(aliceKey2)
            assertKeysEquals(aliceKey1.exportKeys(), aliceKey2!!.exportKeys())
        }
    }
}
