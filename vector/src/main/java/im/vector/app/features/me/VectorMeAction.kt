

package im.vector.app.features.me

import im.vector.app.core.platform.VectorViewModelAction

sealed class VectorMeAction : VectorViewModelAction {
    object RetryFetchingInfo : VectorMeAction()

    data class RefershName(val message: String = "") : VectorMeAction()
}
