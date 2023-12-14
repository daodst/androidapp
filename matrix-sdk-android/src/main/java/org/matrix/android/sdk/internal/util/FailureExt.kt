

package org.matrix.android.sdk.internal.util

import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.internal.di.MoshiProvider


internal fun Throwable.toMatrixErrorStr(): String {
    return (this as? Failure.ServerError)
            ?.let {
                
                val adapter = MoshiProvider.providesMoshi().adapter(MatrixError::class.java)
                tryOrNull { adapter.toJson(error) }
            }
            ?: localizedMessage
            ?: "error"
}
