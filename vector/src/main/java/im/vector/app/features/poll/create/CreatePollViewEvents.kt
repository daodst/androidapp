

package im.vector.app.features.poll.create

import im.vector.app.core.platform.VectorViewEvents

sealed class CreatePollViewEvents : VectorViewEvents {
    object Success : CreatePollViewEvents()
    object EmptyQuestionError : CreatePollViewEvents()
    data class NotEnoughOptionsError(val requiredOptionsCount: Int) : CreatePollViewEvents()
}
