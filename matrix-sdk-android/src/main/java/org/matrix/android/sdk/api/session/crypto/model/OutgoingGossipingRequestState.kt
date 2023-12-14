

package org.matrix.android.sdk.api.session.crypto.model

enum class OutgoingGossipingRequestState {
    UNSENT,
    SENDING,
    SENT,
    CANCELLING,
    CANCELLED,
    FAILED_TO_SEND,
    FAILED_TO_CANCEL
}
