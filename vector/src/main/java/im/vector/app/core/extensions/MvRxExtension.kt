

package im.vector.app.core.extensions

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success


suspend fun <T> tryAsync(block: suspend () -> T): Async<T> {
    return try {
        Success(block.invoke())
    } catch (failure: Throwable) {
        Fail(failure)
    }
}
