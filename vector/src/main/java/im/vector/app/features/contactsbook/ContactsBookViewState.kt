

package im.vector.app.features.contactsbook

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import im.vector.app.core.contacts.MappedContact

data class ContactsBookViewState(
        
        val mappedContacts: Async<List<MappedContact>> = Loading(),
        
        val searchTerm: String = "",
        
        val onlyBoundContacts: Boolean = false,
        
        val filteredMappedContacts: List<MappedContact> = emptyList(),
        
        val isBoundRetrieved: Boolean = false,
        
        val identityServerUrl: String? = null,
        
        val userConsent: Boolean = false
) : MavericksState
