

package im.vector.app.core.utils

import java.util.concurrent.atomic.AtomicBoolean


class ReadOnce<T>(
        private val value: T
) {
    private val valueHasBeenRead = AtomicBoolean(false)

    fun get(): T? {
        return if (valueHasBeenRead.getAndSet(true)) {
            null
        } else {
            value
        }
    }
}


class ReadOnceTrue {
    private val readOnce = ReadOnce(true)

    fun isTrue() = readOnce.get() == true
}
