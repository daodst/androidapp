

package im.vector.app.features.home.room.detail.search

import im.vector.app.core.platform.VectorViewModelAction

sealed class SearchAction : VectorViewModelAction {
    data class SearchWith(val searchTerm: String) : SearchAction()
    object LoadMore : SearchAction()
    object Retry : SearchAction()
}
