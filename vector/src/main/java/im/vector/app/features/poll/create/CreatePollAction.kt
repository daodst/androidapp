

package im.vector.app.features.poll.create

import im.vector.app.core.platform.VectorViewModelAction
import org.matrix.android.sdk.api.session.room.model.message.PollType

sealed class CreatePollAction : VectorViewModelAction {
    data class OnQuestionChanged(val question: String) : CreatePollAction()
    data class OnOptionChanged(val index: Int, val option: String) : CreatePollAction()
    data class OnDeleteOption(val index: Int) : CreatePollAction()
    data class OnPollTypeChanged(val pollType: PollType) : CreatePollAction()
    object OnAddOption : CreatePollAction()
    object OnCreatePoll : CreatePollAction()
}
