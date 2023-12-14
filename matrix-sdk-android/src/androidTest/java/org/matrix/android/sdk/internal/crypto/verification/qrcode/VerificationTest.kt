

package org.matrix.android.sdk.internal.crypto.verification.qrcode

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.amshove.kluent.shouldBe
import org.junit.FixMethodOrder
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.matrix.android.sdk.InstrumentedTest
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.UserPasswordAuth
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.api.session.crypto.verification.PendingVerificationRequest
import org.matrix.android.sdk.api.session.crypto.verification.VerificationMethod
import org.matrix.android.sdk.api.session.crypto.verification.VerificationService
import org.matrix.android.sdk.common.CommonTestHelper
import org.matrix.android.sdk.common.CryptoTestHelper
import org.matrix.android.sdk.common.TestConstants
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.JVM)
@Ignore("This test is flaky ; see issue #5449")
class VerificationTest : InstrumentedTest {

    data class ExpectedResult(
            val sasIsSupported: Boolean = false,
            val otherCanScanQrCode: Boolean = false,
            val otherCanShowQrCode: Boolean = false
    )

    private val sas = listOf(
            VerificationMethod.SAS
    )

    private val sasShow = listOf(
            VerificationMethod.SAS,
            VerificationMethod.QR_CODE_SHOW
    )

    private val sasScan = listOf(
            VerificationMethod.SAS,
            VerificationMethod.QR_CODE_SCAN
    )

    private val sasShowScan = listOf(
            VerificationMethod.SAS,
            VerificationMethod.QR_CODE_SHOW,
            VerificationMethod.QR_CODE_SCAN
    )

    @Test
    fun test_aliceAndBob_sas_sas() = doTest(
            sas,
            sas,
            ExpectedResult(sasIsSupported = true),
            ExpectedResult(sasIsSupported = true)
    )

    @Test
    fun test_aliceAndBob_sas_show() = doTest(
            sas,
            sasShow,
            ExpectedResult(sasIsSupported = true),
            ExpectedResult(sasIsSupported = true)
    )

    @Test
    fun test_aliceAndBob_show_sas() = doTest(
            sasShow,
            sas,
            ExpectedResult(sasIsSupported = true),
            ExpectedResult(sasIsSupported = true)
    )

    @Test
    fun test_aliceAndBob_sas_scan() = doTest(
            sas,
            sasScan,
            ExpectedResult(sasIsSupported = true),
            ExpectedResult(sasIsSupported = true)
    )

    @Test
    fun test_aliceAndBob_scan_sas() = doTest(
            sasScan,
            sas,
            ExpectedResult(sasIsSupported = true),
            ExpectedResult(sasIsSupported = true)
    )

    @Test
    fun test_aliceAndBob_scan_scan() = doTest(
            sasScan,
            sasScan,
            ExpectedResult(sasIsSupported = true),
            ExpectedResult(sasIsSupported = true)
    )

    @Test
    fun test_aliceAndBob_show_show() = doTest(
            sasShow,
            sasShow,
            ExpectedResult(sasIsSupported = true),
            ExpectedResult(sasIsSupported = true)
    )

    @Test
    fun test_aliceAndBob_show_scan() = doTest(
            sasShow,
            sasScan,
            ExpectedResult(sasIsSupported = true, otherCanScanQrCode = true),
            ExpectedResult(sasIsSupported = true, otherCanShowQrCode = true)
    )

    @Test
    fun test_aliceAndBob_scan_show() = doTest(
            sasScan,
            sasShow,
            ExpectedResult(sasIsSupported = true, otherCanShowQrCode = true),
            ExpectedResult(sasIsSupported = true, otherCanScanQrCode = true)
    )

    @Test
    fun test_aliceAndBob_all_all() = doTest(
            sasShowScan,
            sasShowScan,
            ExpectedResult(sasIsSupported = true, otherCanShowQrCode = true, otherCanScanQrCode = true),
            ExpectedResult(sasIsSupported = true, otherCanShowQrCode = true, otherCanScanQrCode = true)
    )

    

    private fun doTest(aliceSupportedMethods: List<VerificationMethod>,
                       bobSupportedMethods: List<VerificationMethod>,
                       expectedResultForAlice: ExpectedResult,
                       expectedResultForBob: ExpectedResult) {
        val testHelper = CommonTestHelper(context())
        val cryptoTestHelper = CryptoTestHelper(testHelper)
        val cryptoTestData = cryptoTestHelper.doE2ETestWithAliceAndBobInARoom()

        val aliceSession = cryptoTestData.firstSession
        val bobSession = cryptoTestData.secondSession!!

        testHelper.doSync<Unit> { callback ->
            aliceSession.cryptoService().crossSigningService()
                    .initializeCrossSigning(
                            object : UserInteractiveAuthInterceptor {
                                override fun performStage(flowResponse: RegistrationFlowResponse, errCode: String?, promise: Continuation<UIABaseAuth>) {
                                    promise.resume(
                                            UserPasswordAuth(
                                                    user = aliceSession.myUserId,
                                                    password = TestConstants.PASSWORD,
                                                    session = flowResponse.session
                                            )
                                    )
                                }
                            }, callback)
        }

        testHelper.doSync<Unit> { callback ->
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
                            }, callback)
        }

        val aliceVerificationService = aliceSession.cryptoService().verificationService()
        val bobVerificationService = bobSession.cryptoService().verificationService()

        var aliceReadyPendingVerificationRequest: PendingVerificationRequest? = null
        var bobReadyPendingVerificationRequest: PendingVerificationRequest? = null

        val latch = CountDownLatch(2)
        val aliceListener = object : VerificationService.Listener {
            override fun verificationRequestUpdated(pr: PendingVerificationRequest) {
                
                if (pr.isReady) {
                    aliceReadyPendingVerificationRequest = pr
                    latch.countDown()
                }
            }
        }
        aliceVerificationService.addListener(aliceListener)

        val bobListener = object : VerificationService.Listener {
            override fun verificationRequestCreated(pr: PendingVerificationRequest) {
                
                bobVerificationService.readyPendingVerificationInDMs(
                        bobSupportedMethods,
                        aliceSession.myUserId,
                        cryptoTestData.roomId,
                        pr.transactionId!!
                )
            }

            override fun verificationRequestUpdated(pr: PendingVerificationRequest) {
                
                if (pr.isReady) {
                    bobReadyPendingVerificationRequest = pr
                    latch.countDown()
                }
            }
        }
        bobVerificationService.addListener(bobListener)

        val bobUserId = bobSession.myUserId
        
        aliceVerificationService.requestKeyVerificationInDMs(aliceSupportedMethods, bobUserId, cryptoTestData.roomId)
        testHelper.await(latch)

        aliceReadyPendingVerificationRequest!!.let { pr ->
            pr.isSasSupported() shouldBe expectedResultForAlice.sasIsSupported
            pr.otherCanShowQrCode() shouldBe expectedResultForAlice.otherCanShowQrCode
            pr.otherCanScanQrCode() shouldBe expectedResultForAlice.otherCanScanQrCode
        }

        bobReadyPendingVerificationRequest!!.let { pr ->
            pr.isSasSupported() shouldBe expectedResultForBob.sasIsSupported
            pr.otherCanShowQrCode() shouldBe expectedResultForBob.otherCanShowQrCode
            pr.otherCanScanQrCode() shouldBe expectedResultForBob.otherCanScanQrCode
        }

        cryptoTestData.cleanUp(testHelper)
    }
}
