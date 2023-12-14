

package im.vector.app.core.mvrx

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success


suspend fun <A> runCatchingToAsync(block: suspend () -> A): Async<A> {
    return runCatching {
        block.invoke()
    }.fold(
            { Success(it) },
            { Fail(it) }
    )
}
