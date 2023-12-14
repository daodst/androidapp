

package im.vector.app.features.createdirect.cluster

import android.net.Uri
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import im.wallet.router.wallet.pojo.EvmosDaoParams
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules

data class ClusterViewState(
        val mode: Int = 0,
        val roomId: String? = null,
        val name: String? = null,
        
        val avatarUri: Uri? = null,
        
        val destoryBalance: String = "",
        val radio: String = "",
        val freezeBalance: String = "",
        val asyncCreateRoomRequest: Async<String> = Uninitialized,
        val salary_range: EvmosDaoParams.NumRange? = null,
        val device_range: EvmosDaoParams.NumRange? = null,
        val create_cluster_min_burn: String? = null,
        val currentRoomJoinRules: RoomJoinRules? = null,
        val isCreated: Boolean = false,
        val isUpdate: Boolean = false,
) : MavericksState {
    constructor(args: ClusterArgs) : this(
            mode = args.mode,
            roomId = args.roomId,
            name = args.name
    )
}
