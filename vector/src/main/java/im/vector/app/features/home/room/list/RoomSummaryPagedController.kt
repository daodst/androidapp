

package im.vector.app.features.home.room.list

import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.paging.PagedListEpoxyController
import im.vector.app.core.utils.createUIHandler
import org.matrix.android.sdk.api.session.room.members.ChangeMembershipState
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class RoomSummaryPagedController(
        private val roomSummaryItemFactory: RoomSummaryItemFactory
) : PagedListEpoxyController<RoomSummary>(
        
        modelBuildingHandler = createUIHandler()
), CollapsableControllerExtension {

    var listener: RoomListListener? = null

    var roomChangeMembershipStates: Map<String, ChangeMembershipState>? = null
        set(value) {
            field = value
            
            requestForcedModelBuild()
        }

    override var collapsed = false
        set(value) {
            if (field != value) {
                field = value
                requestForcedModelBuild()
            }
        }

    override fun addModels(models: List<EpoxyModel<*>>) {
        if (collapsed) {
            super.addModels(emptyList())
        } else {
            super.addModels(models)
        }
    }

    override fun buildItemModel(currentPosition: Int, item: RoomSummary?): EpoxyModel<*> {
        
        item ?: return RoomSummaryItemPlaceHolder_().apply { id(currentPosition) }
        return roomSummaryItemFactory.create(item, roomChangeMembershipStates.orEmpty(), emptySet(), listener)
    }
}
