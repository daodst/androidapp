

package org.matrix.android.sdk.account

import androidx.test.filters.LargeTest
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.CryptoTestHelper
import org.matrix.android.sdk.common.SessionTestParams
import org.matrix.android.sdk.common.TestConstants

@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
@LargeTest
class AccountCreationTest : InstrumentedTest {

    private val commonTestHelper = CommonTestHelper(context())
    private val cryptoTestHelper = CryptoTestHelper(commonTestHelper)

    @Test
    fun createAccountTest() {
        val session = commonTestHelper.createAccount(TestConstants.USER_ALICE, SessionTestParams(withInitialSync = true))

        commonTestHelper.signOutAndClose(session)
    }

    @Test
    @Ignore("This test will be ignored until it is fixed")
    fun createAccountAndLoginAgainTest() {
        val session = commonTestHelper.createAccount(TestConstants.USER_ALICE, SessionTestParams(withInitialSync = true))

        
        val session2 = commonTestHelper.logIntoAccount(session.myUserId, SessionTestParams(withInitialSync = true))

        commonTestHelper.signOutAndClose(session)
        commonTestHelper.signOutAndClose(session2)
    }

    @Test
    fun simpleE2eTest() {
        val res = cryptoTestHelper.doE2ETestWithAliceInARoom()

        res.cleanUp(commonTestHelper)
    }
}
