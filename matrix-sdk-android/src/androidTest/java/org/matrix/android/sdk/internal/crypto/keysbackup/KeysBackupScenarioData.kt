

package org.matrix.android.sdk.internal.crypto.keysbackup

import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.CryptoTestData
import org.matrix.android.sdk.internal.crypto.model.OlmInboundGroupSessionWrapper2


internal data class KeysBackupScenarioData(
        val cryptoTestData: CryptoTestData,
        val aliceKeys: List<OlmInboundGroupSessionWrapper2>,
        val prepareKeysBackupDataResult: PrepareKeysBackupDataResult,
        val aliceSession2: Session
) {
    fun cleanUp(testHelper: CommonTestHelper) {
        cryptoTestData.cleanUp(testHelper)
        testHelper.signOutAndClose(aliceSession2)
    }
}
