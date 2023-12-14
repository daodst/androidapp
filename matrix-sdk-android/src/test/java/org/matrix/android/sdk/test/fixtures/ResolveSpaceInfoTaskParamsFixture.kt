

package org.matrix.android.sdk.test.fixtures

import org.matrix.android.sdk.internal.session.space.ResolveSpaceInfoTask

internal object ResolveSpaceInfoTaskParamsFixture {
    fun aResolveSpaceInfoTaskParams(
            spaceId: String = "",
            limit: Int? = null,
            maxDepth: Int? = null,
            from: String? = null,
            suggestedOnly: Boolean? = null,
    ) = ResolveSpaceInfoTask.Params(
            spaceId,
            limit,
            maxDepth,
            from,
            suggestedOnly,
    )
}
