

package im.vector.app.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

fun <T> Flow<T>.test(scope: CoroutineScope): FlowTestObserver<T> {
    return FlowTestObserver(scope, this)
}

class FlowTestObserver<T>(
        scope: CoroutineScope,
        flow: Flow<T>
) {
    private val values = mutableListOf<T>()
    private val job: Job = flow
            .onEach {
                values.add(it)
            }.launchIn(scope)

    fun assertNoValues() = assertValues(emptyList())

    fun assertValues(vararg values: T) = assertValues(values.toList())

    fun assertValue(position: Int, predicate: (T) -> Boolean): FlowTestObserver<T> {
        assertTrue(predicate(values[position]))
        return this
    }

    fun assertValues(values: List<T>): FlowTestObserver<T> {
        assertEquals(values, this.values)
        return this
    }

    fun finish() {
        job.cancel()
    }
}
