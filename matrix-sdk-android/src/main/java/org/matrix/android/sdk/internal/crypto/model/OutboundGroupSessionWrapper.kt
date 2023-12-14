

package org.matrix.android.sdk.internal.crypto.model

import org.matrix.olm.OlmOutboundGroupSession

internal data class OutboundGroupSessionWrapper(
        val outboundGroupSession: OlmOutboundGroupSession,
        val creationTime: Long
)
