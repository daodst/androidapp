

package org.matrix.android.sdk.account

import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.UserPasswordAuth
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.SessionTestParams
import org.matrix.android.sdk.common.TestConstants
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
class DeactivateAccountTest : InstrumentedTest {

    private val commonTestHelper = CommonTestHelper(context())

    @Test
    fun deactivateAccountTest() {
        val session = commonTestHelper.createAccount(TestConstants.USER_ALICE, SessionTestParams(withInitialSync = true))

        
        commonTestHelper.runBlockingTest {
            session.deactivateAccount(
                    eraseAllData = false,
                    userInteractiveAuthInterceptor = object : UserInteractiveAuthInterceptor {
                        override fun performStage(flowResponse: RegistrationFlowResponse, errCode: String?, promise: Continuation<UIABaseAuth>) {
                            promise.resume(
                                    UserPasswordAuth(
                                            user = session.myUserId,
                                            password = TestConstants.PASSWORD,
                                            session = flowResponse.session
                                    )
                            )
                        }
                    }
            )
        }

        
        val throwable = commonTestHelper.logAccountWithError(session.myUserId, TestConstants.PASSWORD)

        
        assertTrue(throwable is Failure.ServerError &&
                throwable.error.code == MatrixError.M_USER_DEACTIVATED &&
                throwable.error.message == "This account has been deactivated")

        
        val hs = commonTestHelper.createHomeServerConfig()

        commonTestHelper.runBlockingTest {
            commonTestHelper.matrix.authenticationService.getLoginFlow(hs)
        }

        var accountCreationError: Throwable? = null
        commonTestHelper.runBlockingTest {
            try {
                commonTestHelper.matrix.authenticationService
                        .getRegistrationWizard()
                        .createAccount(
                                session.myUserId.substringAfter("@").substringBefore(":"),
                                TestConstants.PASSWORD,
                                null
                        )
            } catch (failure: Throwable) {
                accountCreationError = failure
            }
        }

        
        accountCreationError.let {
            assertTrue(it is Failure.ServerError &&
                    it.error.code == MatrixError.M_USER_IN_USE)
        }

        
    }
}
