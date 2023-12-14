

package org.matrix.android.sdk.internal.session.search

import org.matrix.android.sdk.api.session.search.EventAndSender
import org.matrix.android.sdk.api.session.search.SearchResult
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.model.TimelineEventEntity
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.session.search.response.SearchResponse
import org.matrix.android.sdk.internal.session.search.response.SearchResponseItem
import org.matrix.android.sdk.internal.task.Task
import javax.inject.Inject

internal interface SearchTask : Task<SearchTask.Params, SearchResult> {

    data class Params(
        val searchTerm: String,
        val roomId: String,
        val nextBatch: String? = null,
        val orderByRecent: Boolean,
        val limit: Int,
        val beforeLimit: Int,
        val afterLimit: Int,
        val includeProfile: Boolean
    )
}

internal class DefaultSearchTask @Inject constructor(
    private val searchAPI: SearchAPI,
    private val globalErrorReceiver: GlobalErrorReceiver,
    private val realmSessionProvider: RealmSessionProvider
) : SearchTask {

    override suspend fun execute(params: SearchTask.Params): SearchResult {
        return SearchResult()
        

    }

    @Deprecated(message = "")
    private fun SearchResponse.toDomain(): SearchResult {
        val localTimelineEvents = findRootThreadEventsFromDB(searchCategories.roomEvents?.results)
        return SearchResult(
            nextBatch = searchCategories.roomEvents?.nextBatch,
            highlights = searchCategories.roomEvents?.highlights,
            results = searchCategories.roomEvents?.results?.map { searchResponseItem ->

                val localThreadEventDetails = localTimelineEvents
                    ?.firstOrNull { it.eventId == searchResponseItem.event.eventId }
                    ?.root
                    ?.asDomain()
                    ?.threadDetails

                EventAndSender(
                    searchResponseItem.event.apply {
                        threadDetails = localThreadEventDetails
                    },
                    searchResponseItem.event.senderId?.let { senderId ->
                        searchResponseItem.context?.profileInfo?.get(senderId)
                            ?.let {
                                MatrixItem.UserItem(
                                    senderId,
                                    it["displayname"] as? String,
                                    it["avatar_url"] as? String
                                )
                            }
                    },content = null, body = ""
                )
            }?.reversed()
        )
    }

    
    private fun findRootThreadEventsFromDB(searchResponseItemList: List<SearchResponseItem>?): List<TimelineEventEntity>? {
        return realmSessionProvider.withRealm { realm ->
            searchResponseItemList?.mapNotNull {
                it.event.roomId ?: return@mapNotNull null
                it.event.eventId ?: return@mapNotNull null
                TimelineEventEntity.where(realm, it.event.roomId, it.event.eventId).findFirst()
            }?.filter {
                it.root?.isRootThread == true || it.root?.isThread() == true
            }
        }
    }
}
