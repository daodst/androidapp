

package im.vector.app.features.userdirectory

import androidx.lifecycle.asFlow
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Uninitialized
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.R
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.extensions.isEmail
import im.vector.app.core.extensions.toggle
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.core.resources.StringProvider
import im.vector.app.features.discovery.fetchIdentityServerWithTerms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.MatrixPatterns.getDomain
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.identity.IdentityServiceError
import org.matrix.android.sdk.api.session.identity.IdentityServiceListener
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.util.toMatrixItem

data class ThreePidUser(
        val email: String,
        val user: User?
)

class UserListViewModel @AssistedInject constructor(
        @Assisted initialState: UserListViewState,
        private val stringProvider: StringProvider,
        private val session: Session
) : VectorViewModel<UserListViewState, UserListAction, UserListViewEvents>(initialState) {

    private val knownUsersSearch = MutableStateFlow("")
    private val directoryUsersSearch = MutableStateFlow("")
    private val identityServerUsersSearch = MutableStateFlow(UserSearch(searchTerm = ""))

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<UserListViewModel, UserListViewState> {
        override fun create(initialState: UserListViewState): UserListViewModel
    }

    companion object : MavericksViewModelFactory<UserListViewModel, UserListViewState> by hiltMavericksViewModelFactory()

    private val identityServerListener = object : IdentityServiceListener {
        override fun onIdentityServerChange() {
            withState {
                identityServerUsersSearch.tryEmit(UserSearch(it.searchTerm))
                val identityServerURL = cleanISURL(session.identityService().getCurrentIdentityServerUrl())
                setState {
                    copy(configuredIdentityServer = identityServerURL)
                }
            }
        }
    }

    init {
        observeUsers()
        setState {
            copy(
                    configuredIdentityServer = cleanISURL(session.identityService().getCurrentIdentityServerUrl())
            )
        }
        session.identityService().addListener(identityServerListener)
    }

    private fun cleanISURL(url: String?): String? {
        return url?.removePrefix("https://")
    }

    override fun onCleared() {
        session.identityService().removeListener(identityServerListener)
        super.onCleared()
    }

    override fun handle(action: UserListAction) {
        when (action) {
            is UserListAction.SearchUsers                -> handleSearchUsers(action.value)
            is UserListAction.ClearSearchUsers           -> handleClearSearchUsers()
            is UserListAction.AddPendingSelection        -> handleSelectUser(action)
            is UserListAction.RemovePendingSelection     -> handleRemoveSelectedUser(action)
            UserListAction.ComputeMatrixToLinkForSharing -> handleShareMyMatrixToLink()
            UserListAction.UserConsentRequest            -> handleUserConsentRequest()
            is UserListAction.UpdateUserConsent          -> handleISUpdateConsent(action)
            UserListAction.Resumed                       -> handleResumed()
        }
    }

    private fun handleUserConsentRequest() {
        viewModelScope.launch {
            val event = try {
                val result = session.fetchIdentityServerWithTerms(stringProvider.getString(R.string.resources_language))
                UserListViewEvents.OnPoliciesRetrieved(result)
            } catch (throwable: Throwable) {
                UserListViewEvents.Failure(throwable)
            }
            _viewEvents.post(event)
        }
    }

    private fun handleISUpdateConsent(action: UserListAction.UpdateUserConsent) {
        session.identityService().setUserConsent(action.consent)
        withState {
            retryUserSearch(it)
        }
    }

    private fun handleResumed() {
        withState {
            if (it.hasNoIdentityServerConfigured()) {
                retryUserSearch(it)
            }
        }
    }

    private fun retryUserSearch(state: UserListViewState) {
        identityServerUsersSearch.tryEmit(UserSearch(state.searchTerm, cacheBuster = System.currentTimeMillis()))
    }

    private fun handleSearchUsers(searchTerm: String) {
        setState {
            copy(
                    searchTerm = searchTerm
            )
        }
        if (searchTerm.isEmail().not()) {
            
            
            setState {
                copy(
                        matchingEmail = Uninitialized
                )
            }
        }
        identityServerUsersSearch.tryEmit(UserSearch(searchTerm))
        knownUsersSearch.tryEmit(searchTerm)
        directoryUsersSearch.tryEmit(searchTerm)
    }

    private fun handleShareMyMatrixToLink() {
        session.permalinkService().createPermalink(session.myUserId)?.let {
            _viewEvents.post(UserListViewEvents.OpenShareMatrixToLink(it))
        }
    }

    private fun handleClearSearchUsers() {
        knownUsersSearch.tryEmit("")
        directoryUsersSearch.tryEmit("")
        identityServerUsersSearch.tryEmit(UserSearch(""))
        setState {
            copy(searchTerm = "")
        }
    }

    private fun observeUsers() = withState { state ->
        identityServerUsersSearch
                .filter { it.searchTerm.isEmail() }
                .sample(300)
                .onEach { search ->
                    executeSearchEmail(search.searchTerm)
                }.launchIn(viewModelScope)

        knownUsersSearch
                .sample(300)
                .flatMapLatest { search ->
                    session.getPagedUsersLive(search, state.excludedUserIds).asFlow()
                }
                .execute {
                    copy(knownUsers = it)
                }

        directoryUsersSearch
                .debounce(300)
                .onEach { search ->
                    executeSearchDirectory(state, search)
                }.launchIn(viewModelScope)
    }

    private suspend fun executeSearchEmail(search: String) {
        suspend {
            val params = listOf(ThreePid.Email(search))
            val foundThreePid = session.identityService().lookUp(params).firstOrNull()
            if (foundThreePid == null) {
                ThreePidUser(email = search, user = null)
            } else {
                try {
                    val user = tryOrNull { session.getProfileAsUser(foundThreePid.matrixId) } ?: User(foundThreePid.matrixId)
                    ThreePidUser(
                            email = search,
                            user = user
                    )
                } catch (failure: Throwable) {
                    ThreePidUser(email = search, user = User(foundThreePid.matrixId))
                }
            }
        }.execute {
            copy(matchingEmail = it)
        }
    }

    private suspend fun executeSearchDirectory(state: UserListViewState, search: String) {
        suspend {
            if (search.isBlank()) {
                emptyList()
            } else {
                val searchResult = session
                        .searchUsersDirectory(search, 50, state.excludedUserIds.orEmpty())
                        .sortedBy { it.toMatrixItem().firstLetterOfDisplayName() }
                val userProfile = if (MatrixPatterns.isUserId(search)) {
                    val user = tryOrNull { session.getProfileAsUser(search) }
                    User(
                            userId = search,
                            displayName = user?.displayName,
                            avatarUrl = user?.avatarUrl,
                            tel_numbers = user?.tel_numbers
                    )
                } else {
                    null
                }
                val list = mutableListOf<User>()
                val serverName = session.myUserId.getDomain()
                
                searchResult.forEach { user ->
                    if (!serverName.equals(user.userId.getDomain())) {
                        
                        val nuser = tryOrNull { session.getProfileAsUser(user.userId) }
                        if (nuser != null) {
                            list.add(User(
                                    userId = user.userId,
                                    displayName = nuser?.displayName,
                                    avatarUrl = nuser?.avatarUrl,
                                    tel_numbers = nuser?.tel_numbers
                            ))
                        } else {
                            list.add(user)
                        }
                    } else {
                        list.add(user)
                    }
                }


                if (userProfile == null || list.any { it.userId == userProfile.userId }) {
                    list
                } else {
                    listOf(userProfile) + list
                }
            }
        }.execute {
            copy(directoryUsers = it)
        }
    }

    private fun handleSelectUser(action: UserListAction.AddPendingSelection) = withState { state ->
        val selections = state.pendingSelections.toggle(action.pendingSelection, singleElement = state.singleSelection)
        setState { copy(pendingSelections = selections) }
    }

    private fun handleRemoveSelectedUser(action: UserListAction.RemovePendingSelection) = withState { state ->
        val selections = state.pendingSelections.minus(action.pendingSelection)
        setState { copy(pendingSelections = selections) }
    }
}

private fun UserListViewState.hasNoIdentityServerConfigured() = matchingEmail is Fail && matchingEmail.error == IdentityServiceError.NoIdentityServerConfigured


private data class UserSearch(val searchTerm: String, val cacheBuster: Long = 0)
