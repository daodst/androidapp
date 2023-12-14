

package org.matrix.android.sdk.internal.session.search

import org.matrix.android.sdk.api.session.search.SearchResult
import org.matrix.android.sdk.api.session.search.SearchService
import javax.inject.Inject

internal class DefaultSearchService @Inject constructor(
        private val searchTask: SearchTask
) : SearchService {

    override suspend fun search(searchTerm: String,
                                roomId: String,
                                nextBatch: String?,
                                orderByRecent: Boolean,
                                limit: Int,
                                beforeLimit: Int,
                                afterLimit: Int,
                                includeProfile: Boolean): SearchResult {
        return searchTask.execute(SearchTask.Params(
                searchTerm = searchTerm,
                roomId = roomId,
                nextBatch = nextBatch,
                orderByRecent = orderByRecent,
                limit = limit,
                beforeLimit = beforeLimit,
                afterLimit = afterLimit,
                includeProfile = includeProfile
        ))
    }
}
