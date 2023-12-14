

package im.vector.app.features.call.conference

import im.vector.app.core.platform.VectorViewModelAction

sealed class JitsiCallViewActions : VectorViewModelAction {
    data class SwitchTo(val args: VectorJitsiActivity.Args,
                        val withConfirmation: Boolean) : JitsiCallViewActions()

    
    object OnConferenceLeft : JitsiCallViewActions()
}
