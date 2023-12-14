

package im.vector.app.features.invite

import javax.inject.Inject


interface AutoAcceptInvites {
    
    val isEnabled: Boolean

    
    val hideInvites: Boolean
        get() = isEnabled
}

fun AutoAcceptInvites.showInvites() = !hideInvites


class CompileTimeAutoAcceptInvites @Inject constructor() : AutoAcceptInvites {
    override val isEnabled = false
}
