

package im.vector.app.features.userdirectory

import im.vector.app.core.platform.VectorViewEvents
import im.vector.app.features.discovery.ServerAndPolicies


sealed class UserListViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : UserListViewEvents()
    data class OnPoliciesRetrieved(val identityServerWithTerms: ServerAndPolicies?) : UserListViewEvents()
    data class OpenShareMatrixToLink(val link: String) : UserListViewEvents()
}
