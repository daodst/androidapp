

package org.matrix.android.sdk.api.session.crypto.model

enum class GossipingRequestState {
    NONE,
    PENDING,
    REJECTED,
    ACCEPTING,
    ACCEPTED,
    FAILED_TO_ACCEPTED,

    
    UNABLE_TO_PROCESS,
    CANCELLED_BY_REQUESTER,
    RE_REQUESTED
}
