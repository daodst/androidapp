

package im.vector.app.features.createdirect.cluster

import android.app.Activity
import android.net.Uri
import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules

sealed class ClusterRoomAction : VectorViewModelAction {

    data class SetAvatar(val imageUri: Uri?) : ClusterRoomAction()
    object GetBalance : ClusterRoomAction()

    data class UpGrade(val activity: Activity, val rate: String, val owner: String, val destroy: String, val isDestroy: Boolean) : ClusterRoomAction()
    data class ChangeRoomRule(val rule: RoomJoinRules) : ClusterRoomAction()

    data class Create(val name: String,
                      val topic: String,
                      val rate: String,
                      val owner: String,
                      val destroy: String,
                      val isDestroy: Boolean) : ClusterRoomAction()
}
