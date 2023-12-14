

package im.vector.app.core.extensions

@Suppress("UNCHECKED_CAST") 
inline fun <T, R> Result<T>.andThen(block: (T) -> Result<R>): Result<R> {
    return when (val result = getOrNull()) {
        null -> this as Result<R>
        else -> block(result)
    }
}
