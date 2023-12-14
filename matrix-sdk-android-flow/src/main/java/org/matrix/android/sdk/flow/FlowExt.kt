

package org.matrix.android.sdk.flow

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

internal fun <T> Flow<T>.startWith(dispatcher: CoroutineDispatcher, supplier: suspend () -> T): Flow<T> {
    return onStart {
        val value = withContext(dispatcher) {
            supplier()
        }
        emit(value)
    }
}
