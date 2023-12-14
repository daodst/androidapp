

package org.matrix.android.sdk.test.fixtures

import org.matrix.android.sdk.internal.session.space.SpaceChildSummaryResponse
import org.matrix.android.sdk.internal.session.space.SpacesResponse

internal object SpacesResponseFixture {
    fun aSpacesResponse(
            nextBatch: String? = null,
            rooms: List<SpaceChildSummaryResponse>? = null,
    ) = SpacesResponse(
            nextBatch,
            rooms,
    )
}
