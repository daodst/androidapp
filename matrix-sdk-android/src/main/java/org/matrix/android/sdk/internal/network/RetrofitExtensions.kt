

package org.matrix.android.sdk.internal.network

import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.ResponseBody
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.internal.di.MoshiProvider
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun okhttp3.Call.awaitResponse(): okhttp3.Response {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }

        enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                continuation.resume(response)
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                continuation.resumeWithException(e)
            }
        })
    }
}


internal fun <T> Response<T>.toFailure(globalErrorReceiver: GlobalErrorReceiver?): Failure {
    return toFailure(errorBody(), code(), globalErrorReceiver)
}


internal fun HttpException.toFailure(globalErrorReceiver: GlobalErrorReceiver?): Failure {
    return toFailure(response()?.errorBody(), code(), globalErrorReceiver)
}


internal fun okhttp3.Response.toFailure(globalErrorReceiver: GlobalErrorReceiver?): Failure {
    return toFailure(body, code, globalErrorReceiver)
}

private fun toFailure(errorBody: ResponseBody?, httpCode: Int, globalErrorReceiver: GlobalErrorReceiver?): Failure {
    if (errorBody == null) {
        return Failure.Unknown(RuntimeException("errorBody should not be null"))
    }

    val errorBodyStr = errorBody.string()

    val matrixErrorAdapter = MoshiProvider.providesMoshi().adapter(MatrixError::class.java)

    try {
        val matrixError = matrixErrorAdapter.fromJson(errorBodyStr)

        if (matrixError != null) {
            
            when {
                matrixError.code == MatrixError.M_CONSENT_NOT_GIVEN && !matrixError.consentUri.isNullOrBlank() -> {
                    globalErrorReceiver?.handleGlobalError(GlobalError.ConsentNotGivenError(matrixError.consentUri))
                }
                httpCode == HttpURLConnection.HTTP_UNAUTHORIZED && 
                        matrixError.code == MatrixError.M_UNKNOWN_TOKEN                                        -> {
                    globalErrorReceiver?.handleGlobalError(GlobalError.InvalidToken(matrixError.isSoftLogout.orFalse()))
                }
                matrixError.code == MatrixError.ORG_MATRIX_EXPIRED_ACCOUNT                                     -> {
                    globalErrorReceiver?.handleGlobalError(GlobalError.ExpiredAccount)
                }
            }

            return Failure.ServerError(matrixError, httpCode)
        }
    } catch (ex: Exception) {
        
        Timber.w("The error returned by the server is not a MatrixError")
    } catch (ex: JsonEncodingException) {
        
        Timber.w("The error returned by the server is not a MatrixError, probably HTML string")
    }

    return Failure.OtherServerError(errorBodyStr, httpCode)
}
