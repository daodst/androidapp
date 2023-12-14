

package org.matrix.android.sdk.internal.network

import org.matrix.android.sdk.api.failure.GlobalError

internal interface GlobalErrorReceiver {
    fun handleGlobalError(globalError: GlobalError)
}
