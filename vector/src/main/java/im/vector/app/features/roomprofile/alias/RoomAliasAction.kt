

package im.vector.app.features.roomprofile.alias

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility

sealed class RoomAliasAction : VectorViewModelAction {
    
    object ToggleManualPublishForm : RoomAliasAction()
    data class SetNewAlias(val alias: String) : RoomAliasAction()
    object ManualPublishAlias : RoomAliasAction()
    data class PublishAlias(val alias: String) : RoomAliasAction()
    data class UnpublishAlias(val alias: String) : RoomAliasAction()
    data class SetCanonicalAlias(val canonicalAlias: String?) : RoomAliasAction()

    
    data class SetRoomDirectoryVisibility(val roomDirectoryVisibility: RoomDirectoryVisibility) : RoomAliasAction()

    
    data class RemoveLocalAlias(val alias: String) : RoomAliasAction()
    object ToggleAddLocalAliasForm : RoomAliasAction()
    data class SetNewLocalAliasLocalPart(val aliasLocalPart: String) : RoomAliasAction()
    object AddLocalAlias : RoomAliasAction()

    
    object Retry : RoomAliasAction()
}
