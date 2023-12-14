

package org.matrix.android.sdk.internal.worker

import android.content.Context
import androidx.annotation.CallSuper
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.internal.SessionManager
import org.matrix.android.sdk.internal.session.SessionComponent
import timber.log.Timber


internal abstract class SessionSafeCoroutineWorker<PARAM : SessionWorkerParams>(
        context: Context,
        workerParameters: WorkerParameters,
        private val sessionManager: SessionManager,
        private val paramClass: Class<PARAM>
) : CoroutineWorker(context, workerParameters) {

    @JsonClass(generateAdapter = true)
    internal data class ErrorData(
            override val sessionId: String,
            override val lastFailureMessage: String? = null
    ) : SessionWorkerParams

    final override suspend fun doWork(): Result {
        val params = WorkerParamsFactory.fromData(paramClass, inputData)
                ?: return buildErrorResult(null, "Unable to parse work parameters")
                        .also { Timber.e("Unable to parse work parameters") }

        return try {
            val sessionComponent = sessionManager.getSessionComponent(params.sessionId)
                    ?: return buildErrorResult(params, "No session")

            
            injectWith(sessionComponent)
            if (params.lastFailureMessage != null) {
                
                doOnError(params)
            } else {
                doSafeWork(params)
            }
        } catch (throwable: Throwable) {
            buildErrorResult(params, throwable.localizedMessage ?: "error")
        }
    }

    abstract fun injectWith(injector: SessionComponent)

    
    abstract suspend fun doSafeWork(params: PARAM): Result

    protected fun buildErrorResult(params: PARAM?, message: String): Result {
        return Result.success(
                if (params != null) {
                    WorkerParamsFactory.toData(paramClass, buildErrorParams(params, message))
                } else {
                    WorkerParamsFactory.toData(ErrorData::class.java, ErrorData(sessionId = "", lastFailureMessage = message))
                }
        )
    }

    abstract fun buildErrorParams(params: PARAM, message: String): PARAM

    
    @CallSuper
    open fun doOnError(params: PARAM): Result {
        
        return Result.success(inputData)
                .also { Timber.e("Work cancelled due to input error from parent") }
    }

    companion object {
        fun hasFailed(outputData: Data): Boolean {
            return WorkerParamsFactory.fromData(ErrorData::class.java, outputData)
                    .let { it?.lastFailureMessage != null }
        }
    }
}
