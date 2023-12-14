

package im.vector.app.features.reactions

import im.vector.app.core.platform.VectorViewModelAction

sealed class EmojiSearchAction : VectorViewModelAction {
    data class UpdateQuery(val queryString: String) : EmojiSearchAction()
}
