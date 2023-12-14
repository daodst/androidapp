

package org.matrix.android.sdk.internal.worker


internal interface SessionWorkerParams {
    val sessionId: String

    
    val lastFailureMessage: String?
}
