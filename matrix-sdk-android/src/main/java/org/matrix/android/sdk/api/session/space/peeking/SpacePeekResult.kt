

package org.matrix.android.sdk.api.session.space.peeking

import org.matrix.android.sdk.api.session.room.peeking.PeekResult

sealed class SpacePeekResult {
    abstract class SpacePeekError : SpacePeekResult()
    data class FailedToResolve(val spaceId: String, val roomPeekResult: PeekResult) : SpacePeekError()
    data class NotSpaceType(val spaceId: String) : SpacePeekError()

    data class Success(val summary: SpacePeekSummary) : SpacePeekResult()
}

data class SpacePeekSummary(
        val idOrAlias: String,
        val roomPeekResult: PeekResult.Success,
        val children: List<ISpaceChild>
)

interface ISpaceChild {
    val id: String
    val roomPeekResult: PeekResult
    val order: String?
}

data class SpaceChildPeekResult(
        override val id: String,
        override val roomPeekResult: PeekResult,
        override val order: String? = null
) : ISpaceChild

data class SpaceSubChildPeekResult(
        override val id: String,
        override val roomPeekResult: PeekResult,
        override val order: String?,
        val children: List<ISpaceChild>
) : ISpaceChild
