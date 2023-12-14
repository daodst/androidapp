

package im.vector.app.features.login2.created

import im.vector.app.core.platform.VectorViewEvents


sealed class AccountCreatedViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : AccountCreatedViewEvents()
}
