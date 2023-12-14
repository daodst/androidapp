

package org.matrix.android.sdk.test.fakes

import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver

internal class FakeGlobalErrorReceiver : GlobalErrorReceiver {
    override fun handleGlobalError(globalError: GlobalError) {
        
    }
}
