

package org.matrix.android.sdk.internal.session.room.send.pills

import org.matrix.android.sdk.api.session.room.send.MatrixItemSpan

internal data class MentionLinkSpec(
        val span: MatrixItemSpan,
        val start: Int,
        val end: Int
)
