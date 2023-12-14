

package org.matrix.android.sdk.api.session.pushers

import org.matrix.android.sdk.api.failure.Failure

sealed class PushGatewayFailure : Failure.FeatureFailure() {
    object PusherRejected : PushGatewayFailure()
}
