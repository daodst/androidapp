

package org.matrix.android.sdk.api.network

import org.amshove.kluent.shouldBeEqualTo
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.SessionTestParams
import org.matrix.android.sdk.common.TestConstants
import timber.log.Timber

@RunWith(JUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
class ApiInterceptorTest : InstrumentedTest {

    private val commonTestHelper = CommonTestHelper(context())

    @Test
    fun apiInterceptorTest() {
        val responses = mutableListOf<String>()

        val listener = object : ApiInterceptorListener {
            override fun onApiResponse(path: ApiPath, response: String) {
                Timber.w("onApiResponse($path): $response")
                responses.add(response)
            }
        }

        commonTestHelper.matrix.registerApiInterceptorListener(ApiPath.REGISTER, listener)

        val session = commonTestHelper.createAccount(TestConstants.USER_ALICE, SessionTestParams(withInitialSync = true))

        commonTestHelper.signOutAndClose(session)

        commonTestHelper.matrix.unregisterApiInterceptorListener(ApiPath.REGISTER, listener)

        responses.size shouldBeEqualTo 2
    }
}
