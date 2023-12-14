

package im.vector.app.features.spaces.manage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.PagedList
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksViewModelFactory
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.di.MavericksAssistedViewModelFactory
import im.vector.app.core.di.hiltMavericksViewModelFactory
import im.vector.app.core.platform.VectorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.query.ActiveSpaceFilter
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.query.RoomCategoryFilter
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.RoomSortOrder
import org.matrix.android.sdk.api.session.room.UpdatableLivePageResult
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class AddRoomError(val errorList: Map<String, Throwable>) : Throwable() {
    override fun getLocalizedMessage(): String? {
        return errorList.map { it.value.localizedMessage }.joinToString()
    }
}

class SpaceAddRoomsViewModel @AssistedInject constructor(
        @Assisted val initialState: SpaceAddRoomsState,
        private val session: Session
) : VectorViewModel<SpaceAddRoomsState, SpaceAddRoomActions, SpaceAddRoomsViewEvents>(initialState) {

    @AssistedFactory
    interface Factory : MavericksAssistedViewModelFactory<SpaceAddRoomsViewModel, SpaceAddRoomsState> {
        override fun create(initialState: SpaceAddRoomsState): SpaceAddRoomsViewModel
    }

    companion object : MavericksViewModelFactory<SpaceAddRoomsViewModel, SpaceAddRoomsState> by hiltMavericksViewModelFactory()

    val spaceUpdatableLivePageResult: UpdatableLivePageResult by lazy {
        session.getFilteredPagedRoomSummariesLive(
                roomSummaryQueryParams {
                    this.memberships = listOf(Membership.JOIN)
                    this.excludeType = null
                    this.includeType = listOf(RoomType.SPACE)
                    this.activeSpaceFilter = ActiveSpaceFilter.ExcludeSpace(initialState.spaceId)
                    this.displayName = QueryStringValue.Contains(initialState.currentFilter, QueryStringValue.Case.INSENSITIVE)
                },
                pagedListConfig = PagedList.Config.Builder()
                        .setPageSize(10)
                        .setInitialLoadSizeHint(20)
                        .setEnablePlaceholders(true)
                        .setPrefetchDistance(10)
                        .build(),
                sortOrder = RoomSortOrder.NAME
        )
    }

    val spaceCountFlow: Flow<Int> by lazy {
        spaceUpdatableLivePageResult.livePagedList.asFlow()
                .flatMapLatest { session.getRoomCountLive(spaceUpdatableLivePageResult.queryParams).asFlow() }
                .distinctUntilChanged()
    }

    val roomUpdatableLivePageResult: UpdatableLivePageResult by lazy {
        session.getFilteredPagedRoomSummariesLive(
                roomSummaryQueryParams {
                    this.memberships = listOf(Membership.JOIN)
                    this.excludeType = listOf(RoomType.SPACE)
                    this.includeType = null
                    this.roomCategoryFilter = RoomCategoryFilter.ONLY_ROOMS
                    this.activeSpaceFilter = ActiveSpaceFilter.ExcludeSpace(initialState.spaceId)
                    this.displayName = QueryStringValue.Contains(initialState.currentFilter, QueryStringValue.Case.INSENSITIVE)
                },
                pagedListConfig = PagedList.Config.Builder()
                        .setPageSize(10)
                        .setInitialLoadSizeHint(20)
                        .setEnablePlaceholders(true)
                        .setPrefetchDistance(10)
                        .build(),
                sortOrder = RoomSortOrder.NAME
        )
    }

    val roomCountFlow: Flow<Int> by lazy {
        roomUpdatableLivePageResult.livePagedList.asFlow()
                .flatMapLatest { session.getRoomCountLive(roomUpdatableLivePageResult.queryParams).asFlow() }
                .distinctUntilChanged()
    }

    val dmUpdatableLivePageResult: UpdatableLivePageResult by lazy {
        session.getFilteredPagedRoomSummariesLive(
                roomSummaryQueryParams {
                    this.memberships = listOf(Membership.JOIN)
                    this.excludeType = listOf(RoomType.SPACE)
                    this.includeType = null
                    this.roomCategoryFilter = RoomCategoryFilter.ONLY_DM
                    this.activeSpaceFilter = ActiveSpaceFilter.ExcludeSpace(initialState.spaceId)
                    this.displayName = QueryStringValue.Contains(initialState.currentFilter, QueryStringValue.Case.INSENSITIVE)
                },
                pagedListConfig = PagedList.Config.Builder()
                        .setPageSize(10)
                        .setInitialLoadSizeHint(20)
                        .setEnablePlaceholders(true)
                        .setPrefetchDistance(10)
                        .build(),
                sortOrder = RoomSortOrder.NAME
        )
    }

    val dmCountFlow: Flow<Int> by lazy {
        dmUpdatableLivePageResult.livePagedList.asFlow()
                .flatMapLatest { session.getRoomCountLive(dmUpdatableLivePageResult.queryParams).asFlow() }
                .distinctUntilChanged()
    }

    private val selectionList = mutableMapOf<String, Boolean>()
    val selectionListLiveData = MutableLiveData<Map<String, Boolean>>()

    init {
        val spaceSummary = session.getRoomSummary(initialState.spaceId)
        setState {
            copy(
                    spaceName = spaceSummary?.displayName ?: "",
                    ignoreRooms = (spaceSummary?.flattenParentIds ?: emptyList()) + listOf(initialState.spaceId),
                    shouldShowDMs = !onlyShowSpaces && spaceSummary?.isPublic == false
            )
        }
    }

    fun canGoBack(): Boolean {
        val needToSave = selectionList.values.any { it }
        if (needToSave) {
            _viewEvents.post(SpaceAddRoomsViewEvents.WarnUnsavedChanged)
        }
        return !needToSave
    }

    override fun handle(action: SpaceAddRoomActions) {
        when (action) {
            is SpaceAddRoomActions.UpdateFilter    -> {
                roomUpdatableLivePageResult.queryParams = roomUpdatableLivePageResult.queryParams.copy(
                        displayName = QueryStringValue.Contains(action.filter, QueryStringValue.Case.INSENSITIVE)
                )
                roomUpdatableLivePageResult.queryParams = roomUpdatableLivePageResult.queryParams.copy(
                        displayName = QueryStringValue.Contains(action.filter, QueryStringValue.Case.INSENSITIVE)
                )
                setState {
                    copy(
                            currentFilter = action.filter
                    )
                }
            }
            is SpaceAddRoomActions.ToggleSelection -> {
                selectionList[action.roomSummary.roomId] = (selectionList[action.roomSummary.roomId] ?: false).not()
                selectionListLiveData.postValue(selectionList.toMap())
            }
            SpaceAddRoomActions.Save               -> {
                doAddSelectedRooms()
            }
        }
    }

    private fun doAddSelectedRooms() {
        val childrenToAdd = selectionList.filter { it.value }.keys
        if (childrenToAdd.isEmpty()) return 

        setState {
            copy(
                    isSaving = Loading()
            )
        }
        viewModelScope.launch {
            val errors = mutableMapOf<String, Throwable>()
            val completed = mutableListOf<String>()
            childrenToAdd.forEach { roomId ->
                try {
                    session.spaceService().getSpace(initialState.spaceId)!!.addChildren(
                            roomId = roomId,
                            viaServers = null,
                            order = null
                    )
                    completed.add(roomId)
                } catch (failure: Throwable) {
                    errors[roomId] = failure
                }
            }
            if (errors.isEmpty()) {
                
                withContext(Dispatchers.Main) {
                    setState {
                        copy(
                                isSaving = Success(childrenToAdd.toList())
                        )
                    }
                    completed.forEach {
                        selectionList.remove(it)
                    }
                    _viewEvents.post(SpaceAddRoomsViewEvents.SavedDone)
                }
            } else if (errors.size < childrenToAdd.size) {
                
                withContext(Dispatchers.Main) {
                    setState {
                        copy(
                                isSaving = Success(completed)
                        )
                    }
                    completed.forEach {
                        selectionList.remove(it)
                    }
                    _viewEvents.post(SpaceAddRoomsViewEvents.SavedDone)
                }
            } else {
                
                withContext(Dispatchers.Main) {
                    setState {
                        copy(
                                isSaving = Fail(AddRoomError(errors))
                        )
                    }
                    _viewEvents.post(SpaceAddRoomsViewEvents.SaveFailed(AddRoomError(errors)))
                }
            }
        }
    }
}
