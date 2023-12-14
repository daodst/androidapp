

package org.matrix.android.sdk.internal.network

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.api.failure.getRetryDelay
import org.matrix.android.sdk.api.failure.isLimitExceededError
import org.matrix.android.sdk.api.failure.shouldBeRetried
import org.matrix.android.sdk.internal.network.ssl.CertUtil
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException


internal suspend inline fun <DATA> executeRequest(globalErrorReceiver: GlobalErrorReceiver?,
                                                  canRetry: Boolean = false,
                                                  maxDelayBeforeRetry: Long = 32_000L,
                                                  maxRetriesCount: Int = 4,
                                                  noinline requestBlock: suspend () -> DATA): DATA {
    var currentRetryCount = 0
    var currentDelay = 1_000L

    while (true) {
        try {
            return requestBlock()
        } catch (throwable: Throwable) {
            val exception = when (throwable) {
                is KotlinNullPointerException -> IllegalStateException("The request returned a null body")
                is HttpException              -> throwable.toFailure(globalErrorReceiver)
                else                          -> throwable
            }

            
            val request = (throwable as? HttpException)?.response()?.raw()?.request
            if (request == null) {
                Timber.e("Exception when executing request")
            } else {
                Timber.e("Exception when executing request ${request.method} ${request.url.toString().substringBefore("?")}")
            }

            
            CertUtil.getCertificateException(exception)
                    
                    
                    
                    
                    
                    ?.also { unrecognizedCertificateException -> throw unrecognizedCertificateException }

            currentRetryCount++

            if (exception.isLimitExceededError() && currentRetryCount < maxRetriesCount) {
                
                val retryDelay = exception.getRetryDelay(1_000)
                if (retryDelay <= maxDelayBeforeRetry) {
                    delay(retryDelay)
                } else {
                    
                    throw exception
                }
            } else if (canRetry && currentRetryCount < maxRetriesCount && exception.shouldBeRetried()) {
                delay(currentDelay)
                currentDelay = currentDelay.times(2L).coerceAtMost(maxDelayBeforeRetry)
                
            } else {
                throw when (exception) {
                    is IOException           -> Failure.NetworkConnection(exception)
                    is Failure.ServerError,
                    is Failure.OtherServerError,
                    is CancellationException -> exception
                    else                     -> Failure.Unknown(exception)
                }
            }
        }
    }
}
