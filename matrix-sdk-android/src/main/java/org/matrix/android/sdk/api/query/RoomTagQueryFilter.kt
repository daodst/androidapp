

package org.matrix.android.sdk.api.query

data class RoomTagQueryFilter(
        val isFavorite: Boolean?,
        val isLowPriority: Boolean?,
        val isServerNotice: Boolean?
)
