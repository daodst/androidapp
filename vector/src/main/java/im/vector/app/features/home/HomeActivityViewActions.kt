

package im.vector.app.features.home

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.room.model.RoomSummary

sealed interface HomeActivityViewActions : VectorViewModelAction {
    object ViewStarted : HomeActivityViewActions
    data class RefersRoomList(val list: List<RoomSummary>) : HomeActivityViewActions
    object PushPromptHasBeenReviewed : HomeActivityViewActions
}
