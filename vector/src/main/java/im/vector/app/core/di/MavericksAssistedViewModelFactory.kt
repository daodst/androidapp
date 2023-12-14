

package im.vector.app.core.di

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel


interface MavericksAssistedViewModelFactory<VM : MavericksViewModel<S>, S : MavericksState> {
    fun create(initialState: S): VM
}
