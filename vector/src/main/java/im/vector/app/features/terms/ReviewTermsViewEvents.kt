

package im.vector.app.features.terms

import im.vector.app.core.platform.VectorViewEvents

sealed class ReviewTermsViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : ReviewTermsViewEvents()
    data class Failure(val throwable: Throwable, val finish: Boolean) : ReviewTermsViewEvents()
    object Success : ReviewTermsViewEvents()
}
