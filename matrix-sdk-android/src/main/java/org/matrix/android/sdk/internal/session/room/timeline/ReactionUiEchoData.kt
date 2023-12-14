

package org.matrix.android.sdk.internal.session.room.timeline

internal data class ReactionUiEchoData(
        val localEchoId: String,
        val reactedOnEventId: String,
        val reaction: String
)
