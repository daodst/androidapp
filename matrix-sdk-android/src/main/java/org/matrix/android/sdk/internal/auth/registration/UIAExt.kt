

package org.matrix.android.sdk.internal.auth.registration

import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.toRegistrationFlowResponse
import timber.log.Timber
import kotlin.coroutines.suspendCoroutine


internal suspend fun handleUIA(failure: Throwable,
                               interceptor: UserInteractiveAuthInterceptor,
                               retryBlock: suspend (UIABaseAuth) -> Unit): Boolean {
    Timber.d("## UIA: check error ${failure.message}")
    val flowResponse = failure.toRegistrationFlowResponse()
            ?: return false.also {
                Timber.d("## UIA: not a UIA error")
            }

    Timber.d("## UIA: error can be passed to interceptor")
    Timber.d("## UIA: type = ${flowResponse.flows}")

    Timber.d("## UIA: delegate to interceptor...")
    val authUpdate = try {
        suspendCoroutine<UIABaseAuth> { continuation ->
            interceptor.performStage(flowResponse, (failure as? Failure.ServerError)?.error?.code, continuation)
        }
    } catch (failure2: Throwable) {
        Timber.w(failure2, "## UIA: failed to participate")
        return false
    }

    Timber.d("## UIA: updated auth")
    return try {
        retryBlock(authUpdate)
        true
    } catch (failure3: Throwable) {
        handleUIA(failure3, interceptor, retryBlock)
    }
}
