

package org.matrix.android.sdk.common

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.Configuration
import androidx.work.WorkManager
import com.zhuinden.monarchy.Monarchy
import org.matrix.android.sdk.BuildConfig
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.auth.AuthenticationService
import org.matrix.android.sdk.api.auth.HomeServerHistoryService
import org.matrix.android.sdk.api.legacy.LegacySessionImporter
import org.matrix.android.sdk.api.network.ApiInterceptorListener
import org.matrix.android.sdk.api.network.ApiPath
import org.matrix.android.sdk.api.raw.RawService
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.network.ApiInterceptor
import org.matrix.android.sdk.internal.network.UserAgentHolder
import org.matrix.android.sdk.internal.util.BackgroundDetectionObserver
import org.matrix.android.sdk.internal.worker.MatrixWorkerFactory
import org.matrix.olm.OlmManager
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


internal class TestMatrix constructor(context: Context, matrixConfiguration: MatrixConfiguration) {

    @Inject internal lateinit var legacySessionImporter: LegacySessionImporter
    @Inject internal lateinit var authenticationService: AuthenticationService
    @Inject internal lateinit var rawService: RawService
    @Inject internal lateinit var userAgentHolder: UserAgentHolder
    @Inject internal lateinit var backgroundDetectionObserver: BackgroundDetectionObserver
    @Inject internal lateinit var olmManager: OlmManager
    @Inject internal lateinit var sessionManager: SessionManager
    @Inject internal lateinit var homeServerHistoryService: HomeServerHistoryService
    @Inject internal lateinit var apiInterceptor: ApiInterceptor
    @Inject internal lateinit var matrixWorkerFactory: MatrixWorkerFactory

    private val uiHandler = Handler(Looper.getMainLooper())

    init {
        Monarchy.init(context)
        DaggerTestMatrixComponent.factory().create(context, matrixConfiguration).inject(this)
        val configuration = Configuration.Builder()
                .setExecutor(Executors.newCachedThreadPool())
                .setWorkerFactory(matrixWorkerFactory)
                .build()
        WorkManager.initialize(context, configuration)
        uiHandler.post {
            ProcessLifecycleOwner.get().lifecycle.addObserver(backgroundDetectionObserver)
        }
    }

    fun getUserAgent() = userAgentHolder.userAgent

    fun authenticationService(): AuthenticationService {
        return authenticationService
    }

    fun rawService() = rawService

    fun homeServerHistoryService() = homeServerHistoryService

    fun legacySessionImporter(): LegacySessionImporter {
        return legacySessionImporter
    }

    fun registerApiInterceptorListener(path: ApiPath, listener: ApiInterceptorListener) {
        apiInterceptor.addListener(path, listener)
    }

    fun unregisterApiInterceptorListener(path: ApiPath, listener: ApiInterceptorListener) {
        apiInterceptor.removeListener(path, listener)
    }

    companion object {

        private lateinit var instance: TestMatrix
        private val isInit = AtomicBoolean(false)

        fun initialize(context: Context, matrixConfiguration: MatrixConfiguration) {
            if (isInit.compareAndSet(false, true)) {
                instance = TestMatrix(context.applicationContext, matrixConfiguration)
            }
        }

        fun getInstance(): TestMatrix {
            if (isInit.compareAndSet(false, false)) {
                throw IllegalStateException("Matrix is not initialized properly. You should call TestMatrix.initialize first")
            }
            return instance
        }

        fun getSdkVersion(): String {
            return BuildConfig.SDK_VERSION + " (" + BuildConfig.GIT_SDK_REVISION + ")"
        }
    }
}
