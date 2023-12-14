

package org.matrix.android.sdk.account

import org.amshove.kluent.shouldBeTrue
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.failure.isInvalidPassword
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.SessionTestParams
import org.matrix.android.sdk.common.TestConstants

@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
@Ignore("This test will be ignored until it is fixed")
class ChangePasswordTest : InstrumentedTest {

    private val commonTestHelper = CommonTestHelper(context())

    companion object {
        private const val NEW_PASSWORD = "this is a new password"
    }

    @Test
    fun changePasswordTest() {
        val session = commonTestHelper.createAccount(TestConstants.USER_ALICE, SessionTestParams(withInitialSync = false))

        
        commonTestHelper.runBlockingTest {
            session.changePassword(TestConstants.PASSWORD, NEW_PASSWORD)
        }

        
        val throwable = commonTestHelper.logAccountWithError(session.myUserId, TestConstants.PASSWORD)
        throwable.isInvalidPassword().shouldBeTrue()

        
        val session2 = commonTestHelper.logIntoAccount(session.myUserId, NEW_PASSWORD, SessionTestParams(withInitialSync = false))

        commonTestHelper.signOutAndClose(session)
        commonTestHelper.signOutAndClose(session2)
    }
}
