

package org.matrix.android.sdk.common

import org.matrix.android.sdk.api.session.Session

data class CryptoTestData(val roomId: String,
                          val sessions: List<Session>) {

    val firstSession: Session
        get() = sessions.first()

    val secondSession: Session?
        get() = sessions.getOrNull(1)

    val thirdSession: Session?
        get() = sessions.getOrNull(2)

    fun cleanUp(testHelper: CommonTestHelper) {
        sessions.forEach {
            testHelper.signOutAndClose(it)
        }
    }
}
