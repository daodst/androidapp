

package im.vector.app.features.pay4invite

import im.vector.app.core.platform.VectorViewEvents


sealed class Pay4InviteViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : Pay4InviteViewEvents()
    data class Failure(val throwable: Throwable) : Pay4InviteViewEvents()



}
