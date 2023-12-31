

package org.matrix.android.sdk.internal.di

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.MatrixCoroutineDispatchers
import org.matrix.android.sdk.internal.session.SessionScope
import org.matrix.android.sdk.internal.worker.MatrixWorkerFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SessionScope
internal class WorkManagerProvider @Inject constructor(
        context: Context,
        @SessionId private val sessionId: String,
        private val coroutineDispatchers: MatrixCoroutineDispatchers,
        private val sessionScope: CoroutineScope
) {
    private val tag = MATRIX_SDK_TAG_PREFIX + sessionId

    val workManager = WorkManager.getInstance(context)

    init {
        checkIfWorkerFactoryIsSetup()
    }

    
    inline fun <reified W : ListenableWorker> matrixOneTimeWorkRequestBuilder() =
            OneTimeWorkRequestBuilder<W>()
                    .addTag(tag)

    
    inline fun <reified W : ListenableWorker> matrixPeriodicWorkRequestBuilder(repeatInterval: Long,
                                                                               repeatIntervalTimeUnit: TimeUnit) =
            PeriodicWorkRequestBuilder<W>(repeatInterval, repeatIntervalTimeUnit)
                    .addTag(tag)

    
    fun cancelAllWorks() {
        workManager.let {
            it.cancelAllWorkByTag(tag)
            it.pruneWork()
        }
    }

    private fun checkIfWorkerFactoryIsSetup() {
        sessionScope.launch(coroutineDispatchers.main) {
            val checkWorkerRequest = OneTimeWorkRequestBuilder<MatrixWorkerFactory.CheckFactoryWorker>().build()
            workManager.enqueue(checkWorkerRequest)
            val checkWorkerLiveState = workManager.getWorkInfoByIdLiveData(checkWorkerRequest.id)
            val observer = object : Observer<WorkInfo> {
                override fun onChanged(workInfo: WorkInfo?) {
                    if (workInfo?.state?.isFinished == true) {
                        checkWorkerLiveState.removeObserver(this)
                        if (workInfo.state == WorkInfo.State.FAILED) {
                            throw RuntimeException("MatrixWorkerFactory is not being set on your worker configuration.\n" +
                                    "Makes sure to add it to a DelegatingWorkerFactory if you have your own factory or use it directly.\n" +
                                    "You can grab the instance through the Matrix class.")
                        }
                    }
                }
            }
            checkWorkerLiveState.observeForever(observer)
        }
    }

    companion object {
        private const val MATRIX_SDK_TAG_PREFIX = "MatrixSDK-"

        
        val workConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        
        const val BACKOFF_DELAY_MILLIS = WorkRequest.MIN_BACKOFF_MILLIS
    }
}
