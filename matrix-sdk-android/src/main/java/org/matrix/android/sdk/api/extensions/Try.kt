

package org.matrix.android.sdk.api.extensions

import timber.log.Timber

inline fun <A> tryOrNull(message: String? = null, operation: () -> A): A? {
    return try {
        operation()
    } catch (any: Throwable) {
        if (message != null) {
            Timber.e(any, message)
        }
        null
    }
}
