

package org.matrix.android.sdk.api.session.search


interface SearchService {

    
    suspend fun search(searchTerm: String,
                       roomId: String,
                       nextBatch: String?,
                       orderByRecent: Boolean,
                       limit: Int,
                       beforeLimit: Int,
                       afterLimit: Int,
                       includeProfile: Boolean): SearchResult
}
