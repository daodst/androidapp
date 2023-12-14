

package im.vector.app.test

import com.airbnb.mvrx.MavericksState
import im.vector.app.core.platform.VectorViewEvents
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.platform.VectorViewModelAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

fun String.trimIndentOneLine() = trimIndent().replace("\n", "")

fun <S : MavericksState, VA : VectorViewModelAction, VE : VectorViewEvents> VectorViewModel<S, VA, VE>.test(): ViewModelTest<S, VE> {
    val testResultCollectingScope = CoroutineScope(Dispatchers.Unconfined)
    val state = stateFlow.test(testResultCollectingScope)
    val viewEvents = viewEvents.stream().test(testResultCollectingScope)
    return ViewModelTest(state, viewEvents)
}

class ViewModelTest<S, VE>(
        val states: FlowTestObserver<S>,
        val viewEvents: FlowTestObserver<VE>
) {

    fun assertNoEvents(): ViewModelTest<S, VE> {
        viewEvents.assertNoValues()
        return this
    }

    fun assertEvents(vararg expected: VE): ViewModelTest<S, VE> {
        viewEvents.assertValues(*expected)
        return this
    }

    fun assertEvent(position: Int = 0, predicate: (VE) -> Boolean): ViewModelTest<S, VE> {
        viewEvents.assertValue(position, predicate)
        return this
    }

    fun assertStates(vararg expected: S): ViewModelTest<S, VE> {
        states.assertValues(*expected)
        return this
    }

    fun assertStatesChanges(initial: S, vararg expected: S.() -> S): ViewModelTest<S, VE> {
        return assertStatesChanges(initial, expected.toList())
    }

    
    fun assertStatesChanges(initial: S, expected: List<S.() -> S>): ViewModelTest<S, VE> {
        val reducedExpectedStates = expected.fold(mutableListOf(initial)) { acc, curr ->
            val next = curr.invoke(acc.last())
            acc.add(next)
            acc
        }

        states.assertValues(reducedExpectedStates)
        return this
    }

    fun assertStates(expected: List<S>): ViewModelTest<S, VE> {
        states.assertValues(expected)
        return this
    }

    fun assertState(expected: S): ViewModelTest<S, VE> {
        states.assertValues(expected)
        return this
    }

    fun finish() {
        states.finish()
        viewEvents.finish()
    }
}
