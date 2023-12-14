

package im.vector.app.features.spaces.explore

import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.VisibilityState
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Incomplete
import com.airbnb.mvrx.Uninitialized
import im.vector.app.R
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.errorWithRetryItem
import im.vector.app.core.epoxy.loadingItem
import im.vector.app.core.error.ErrorFormatter
import im.vector.app.core.resources.ColorProvider
import im.vector.app.core.resources.StringProvider
import im.vector.app.core.ui.list.Action
import im.vector.app.core.ui.list.genericEmptyWithActionItem
import im.vector.app.core.ui.list.genericPillItem
import im.vector.app.features.home.AvatarRenderer
import im.vector.app.features.home.room.list.spaceChildInfoItem
import im.vector.lib.core.utils.epoxy.charsequence.toEpoxyCharSequence
import me.gujun.android.span.span
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError.Companion.M_UNRECOGNIZED
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

class SpaceDirectoryController @Inject constructor(
        private val avatarRenderer: AvatarRenderer,
        private val stringProvider: StringProvider,
        private val colorProvider: ColorProvider,
        private val errorFormatter: ErrorFormatter
) : TypedEpoxyController<SpaceDirectoryState>() {

    interface InteractionListener {
        fun onButtonClick(spaceChildInfo: SpaceChildInfo)
        fun onSpaceChildClick(spaceChildInfo: SpaceChildInfo)
        fun onRoomClick(spaceChildInfo: SpaceChildInfo)
        fun retry()
        fun addExistingRooms(spaceId: String)
        fun loadAdditionalItemsIfNeeded()
    }

    var listener: InteractionListener? = null

    override fun buildModels(data: SpaceDirectoryState?) {
        val host = this
        val currentRootId = data?.hierarchyStack?.lastOrNull() ?: data?.spaceId ?: return
        val results = data?.apiResults?.get(currentRootId)

        if (results is Incomplete) {
            loadingItem {
                id("loading")
            }
        } else if (results is Fail) {
            val failure = results.error
            if (failure is Failure.ServerError && failure.error.code == M_UNRECOGNIZED) {
                genericPillItem {
                    id("HS no Support")
                    imageRes(R.drawable.error)
                    tintIcon(false)
                    text(
                            span {
                                span(host.stringProvider.getString(R.string.spaces_no_server_support_title)) {
                                    textStyle = "bold"
                                    textColor = host.colorProvider.getColorFromAttribute(R.attr.vctr_content_primary)
                                }
                                +"\n\n"
                                span(host.stringProvider.getString(R.string.spaces_no_server_support_description)) {
                                    textColor = host.colorProvider.getColorFromAttribute(R.attr.vctr_content_secondary)
                                }
                            }.toEpoxyCharSequence()
                    )
                }
            } else {
                errorWithRetryItem {
                    id("api_err")
                    text(host.errorFormatter.toHumanReadable(failure))
                    listener { host.listener?.retry() }
                }
            }
        } else {
            val hierarchySummary = results?.invoke()
            val flattenChildInfo = hierarchySummary
                    ?.children
                    ?.filter {
                        it.parentRoomId == (data.hierarchyStack.lastOrNull() ?: data.spaceId)
                    }
                    ?: emptyList()

            if (flattenChildInfo.isEmpty()) {
                genericEmptyWithActionItem {
                    id("empty_res")
                    title(host.stringProvider.getString(R.string.this_space_has_no_rooms))
                    iconRes(R.drawable.ic_empty_icon_room)
                    iconTint(host.colorProvider.getColorFromAttribute(R.attr.vctr_reaction_background_on))
                    apply {
                        if (data?.canAddRooms == true) {
                            description(host.stringProvider.getString(R.string.this_space_has_no_rooms_admin))
                            buttonAction(
                                    Action(
                                            title = host.stringProvider.getString(R.string.space_add_existing_rooms),
                                            listener = object : ClickListener {
                                                override fun invoke(p1: View) {
                                                    host.listener?.addExistingRooms(data.spaceId)
                                                }
                                            }
                                    ))
                        } else {
                            description(host.stringProvider.getString(R.string.this_space_has_no_rooms_not_admin))
                        }
                    }
                }
            } else {
                flattenChildInfo.forEach { info ->
                    val isSpace = info.roomType == RoomType.SPACE
                    val isJoined = data?.joinedRoomsIds?.contains(info.childRoomId) == true
                    val isLoading = data?.changeMembershipStates?.get(info.childRoomId)?.isInProgress() ?: false
                    val error = (data?.changeMembershipStates?.get(info.childRoomId) as? ChangeMembershipState.FailedJoining)?.throwable
                    
                    val matrixItem = data?.knownRoomSummaries?.find { it.roomId == info.childRoomId }?.toMatrixItem()
                            ?: info.toMatrixItem()

                    spaceChildInfoItem {
                        id(info.childRoomId)
                        matrixItem(matrixItem)
                        avatarRenderer(host.avatarRenderer)
                        topic(info.topic)
                        errorLabel(
                                error?.let {
                                    host.stringProvider.getString(R.string.error_failed_to_join_room, host.errorFormatter.toHumanReadable(it))
                                }
                        )
                        memberCount(info.activeMemberCount ?: 0)
                        loading(isLoading)
                        buttonLabel(
                                when {
                                    error != null -> host.stringProvider.getString(R.string.global_retry)
                                    isJoined      -> host.stringProvider.getString(R.string.action_open)
                                    else          -> host.stringProvider.getString(R.string.action_join)
                                }
                        )
                        apply {
                            if (isSpace) {
                                itemClickListener { host.listener?.onSpaceChildClick(info) }
                            } else {
                                itemClickListener { host.listener?.onRoomClick(info) }
                            }
                        }
                        buttonClickListener { host.listener?.onButtonClick(info) }
                    }
                }
            }
            if (hierarchySummary?.nextToken != null) {
                val paginationStatus = data.paginationStatus[currentRootId] ?: Uninitialized
                if (paginationStatus is Fail) {
                    errorWithRetryItem {
                        id("error_${currentRootId}_${hierarchySummary.nextToken}")
                        text(host.errorFormatter.toHumanReadable(paginationStatus.error))
                        listener { host.listener?.loadAdditionalItemsIfNeeded() }
                    }
                } else {
                    loadingItem {
                        id("pagination_${currentRootId}_${hierarchySummary.nextToken}")
                        showLoader(true)
                        onVisibilityStateChanged { _, _, visibilityState ->
                            
                            if (visibilityState == VisibilityState.VISIBLE) {
                                
                                host.listener?.loadAdditionalItemsIfNeeded()
                            }
                        }
                    }
                }
            }
        }
    }
}
