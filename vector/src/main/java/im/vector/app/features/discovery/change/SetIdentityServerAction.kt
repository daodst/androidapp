

package im.vector.app.features.discovery.change

import im.vector.app.core.platform.VectorViewModelAction

sealed class SetIdentityServerAction : VectorViewModelAction {
    object UseDefaultIdentityServer : SetIdentityServerAction()

    data class UseCustomIdentityServer(val url: String) : SetIdentityServerAction()
}
