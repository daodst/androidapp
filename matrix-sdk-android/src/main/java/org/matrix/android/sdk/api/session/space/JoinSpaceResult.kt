

package org.matrix.android.sdk.api.session.space

sealed class JoinSpaceResult {
    object Success : JoinSpaceResult()
    data class Fail(val error: Throwable) : JoinSpaceResult()

    
    data class PartialSuccess(val failedRooms: Map<String, Throwable>) : JoinSpaceResult()

    fun isSuccess() = this is Success || this is PartialSuccess
}
