

package im.vector.app.features.home.room.detail.search

import im.vector.app.core.platform.VectorViewEvents

sealed class SearchViewEvents : VectorViewEvents {
    data class Failure(val throwable: Throwable) : SearchViewEvents()
}
