

package im.vector.app

import android.net.Uri
import androidx.lifecycle.Observer
import im.vector.app.ui.robot.OnboardingRobot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixCallback
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.sync.SyncState
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

abstract class VerificationTestBase {

    val password = "password"
    val homeServerUrl: String = "http://10.0.2.2:8080"

    protected val uiTestBase = OnboardingRobot()

    fun createAccountAndSync(matrix: Matrix,
                             userName: String,
                             password: String,
                             withInitialSync: Boolean): Session {
        val hs = createHomeServerConfig()

        runBlockingTest {
            matrix.authenticationService().getLoginFlow(hs)
        }

        runBlockingTest {
            matrix.authenticationService()
                    .getRegistrationWizard()
                    .createAccount(userName, password, null)
        }

        
        val registrationResult = runBlockingTest {
            matrix.authenticationService()
                    .getRegistrationWizard()
                    .dummy()
        }

        Assert.assertTrue(registrationResult is RegistrationResult.Success)
        val session = (registrationResult as RegistrationResult.Success).session
        if (withInitialSync) {
            syncSession(session)
        }

        return session
    }

    private fun createHomeServerConfig(): HomeServerConnectionConfig {
        return HomeServerConnectionConfig.Builder()
                .withHomeServerUri(Uri.parse(homeServerUrl))
                .build()
    }

    protected fun <T> runBlockingTest(timeout: Long = 20_000, block: suspend () -> T): T {
        return runBlocking {
            withTimeout(timeout) {
                block()
            }
        }
    }

    
    inline fun <reified T> doSync(block: (MatrixCallback<T>) -> Unit): T {
        val lock = CountDownLatch(1)
        var result: T? = null

        val callback = object : TestMatrixCallback<T>(lock) {
            override fun onSuccess(data: T) {
                result = data
                super.onSuccess(data)
            }
        }

        block.invoke(callback)

        lock.await(20_000, TimeUnit.MILLISECONDS)

        Assert.assertNotNull(result)
        return result!!
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    private fun syncSession(session: Session) {
        val lock = CountDownLatch(1)

        GlobalScope.launch(Dispatchers.Main) { session.open() }

        session.startSync(true)

        val syncLiveData = runBlocking(Dispatchers.Main) {
            session.getSyncStateLive()
        }
        val syncObserver = object : Observer<SyncState> {
            override fun onChanged(t: SyncState?) {
                if (session.hasAlreadySynced()) {
                    lock.countDown()
                    syncLiveData.removeObserver(this)
                }
            }
        }
        GlobalScope.launch(Dispatchers.Main) { syncLiveData.observeForever(syncObserver) }

        lock.await(20_000, TimeUnit.MILLISECONDS)
    }
}
