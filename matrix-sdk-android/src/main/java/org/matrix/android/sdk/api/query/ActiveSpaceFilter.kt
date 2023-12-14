

package org.matrix.android.sdk.api.query

sealed class ActiveSpaceFilter {
    object None : ActiveSpaceFilter()
    data class ActiveSpace(val currentSpaceId: String?) : ActiveSpaceFilter()
    data class ExcludeSpace(val spaceId: String) : ActiveSpaceFilter()
}
