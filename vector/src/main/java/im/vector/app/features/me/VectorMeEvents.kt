

package im.vector.app.features.me

import im.vector.app.core.platform.VectorViewEvents


sealed class VectorMeEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : VectorMeEvents()
    object DisLoading : VectorMeEvents()
    data class Failure(val throwable: Throwable) : VectorMeEvents()
}
