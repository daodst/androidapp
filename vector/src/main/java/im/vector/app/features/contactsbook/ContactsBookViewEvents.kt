

package im.vector.app.features.contactsbook

import im.vector.app.core.platform.VectorViewEvents
import im.vector.app.features.discovery.ServerAndPolicies

sealed class ContactsBookViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : ContactsBookViewEvents()
    data class OnPoliciesRetrieved(val identityServerWithTerms: ServerAndPolicies?) : ContactsBookViewEvents()
}
