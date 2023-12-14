

package org.matrix.android.sdk.internal.session.profile

import org.matrix.android.sdk.api.session.identity.ThreePid

internal data class PendingThreePid(
        val threePid: ThreePid,
        val clientSecret: String,
        val sendAttempt: Int,
        
        val sid: String,
        
        val submitUrl: String?
)
