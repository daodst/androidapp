

package im.vector.app.features.call.transfer

import im.vector.app.core.platform.VectorViewEvents

sealed class CallTransferViewEvents : VectorViewEvents {
    object Complete : CallTransferViewEvents()
}
