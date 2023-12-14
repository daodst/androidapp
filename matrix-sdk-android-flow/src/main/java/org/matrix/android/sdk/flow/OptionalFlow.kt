

package org.matrix.android.sdk.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.matrix.android.sdk.api.util.Optional

fun <T : Any> Flow<Optional<T>>.unwrap(): Flow<T> {
    return filter { it.hasValue() }.map { it.get() }
}

fun <T : Any, U : Any> Flow<Optional<T>>.mapOptional(fn: (T) -> U?): Flow<Optional<U>> {
    return map {
        it.map(fn)
    }
}
