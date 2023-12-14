

package im.vector.app.features.discovery

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Uninitialized
import org.matrix.android.sdk.api.session.identity.SharedState
import org.matrix.android.sdk.api.session.identity.ThreePid

data class PidInfo(
        
        val threePid: ThreePid,
        
        val isShared: Async<SharedState>,
        
        
        val finalRequest: Async<Unit> = Uninitialized
)
