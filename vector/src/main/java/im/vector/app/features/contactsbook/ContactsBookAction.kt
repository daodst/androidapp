

package im.vector.app.features.contactsbook

import im.vector.app.core.platform.VectorViewModelAction

sealed class ContactsBookAction : VectorViewModelAction {
    data class FilterWith(val filter: String) : ContactsBookAction()
    data class OnlyBoundContacts(val onlyBoundContacts: Boolean) : ContactsBookAction()
    object UserConsentRequest : ContactsBookAction()
    object UserConsentGranted : ContactsBookAction()
}
