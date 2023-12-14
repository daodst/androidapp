

package org.matrix.android.sdk.internal.crypto.verification

import org.matrix.android.sdk.api.session.crypto.verification.VerificationState
import org.matrix.android.sdk.api.session.crypto.verification.isCanceled

internal fun VerificationState?.toState(newState: VerificationState): VerificationState {
    
    
    
    if (newState.isCanceled()) {
        return newState
    }
    
    if (this?.isCanceled() == true) {
        return this
    }
    return newState
}
