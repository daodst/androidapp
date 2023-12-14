

package im.vector.app.core.ui.bottomsheet

import com.airbnb.mvrx.MavericksState
import im.vector.app.core.platform.EmptyAction
import im.vector.app.core.platform.EmptyViewEvents
import im.vector.app.core.platform.VectorViewModel

abstract class BottomSheetGenericViewModel<State : MavericksState>(initialState: State) :
        VectorViewModel<State, EmptyAction, EmptyViewEvents>(initialState) {

    override fun handle(action: EmptyAction) {
        
    }
}
