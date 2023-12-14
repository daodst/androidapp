

package im.vector.app.features.spaces.create

import android.net.Uri
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized

data class CreateSpaceState(
        val name: String? = null,
        val avatarUri: Uri? = null,
        val topic: String = "",
        val step: Step = Step.ChooseType,
        val spaceType: SpaceType? = null,
        val spaceTopology: SpaceTopology? = null,
        val homeServerName: String = "",
        val aliasLocalPart: String? = null,
        val aliasManuallyModified: Boolean = false,
        val aliasVerificationTask: Async<Boolean> = Uninitialized,
        val nameInlineError: String? = null,
        val defaultRooms: Map<Int, String?>? = null, 
        val default3pidInvite: Map<Int, String?>? = null, 
        val emailValidationResult: Map<Int, Boolean>? = null, 
        val creationResult: Async<String> = Uninitialized,
        val canInviteByMail: Boolean = false
) : MavericksState {

    enum class Step {
        ChooseType,
        SetDetails,
        AddRooms,
        ChoosePrivateType,
        AddEmailsOrInvites
    }
}
