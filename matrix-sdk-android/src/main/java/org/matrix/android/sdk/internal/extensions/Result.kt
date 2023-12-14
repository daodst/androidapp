
package org.matrix.android.sdk.internal.extensions

import org.matrix.android.sdk.api.MatrixCallback

internal fun <A> Result<A>.foldToCallback(callback: MatrixCallback<A>): Unit = fold(
        { callback.onSuccess(it) },
        { callback.onFailure(it) }
)
