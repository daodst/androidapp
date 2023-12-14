

package org.matrix.android.sdk.internal.session.identity.data

internal data class IdentityPendingBinding(
        
        val clientSecret: String,
        
        val sendAttempt: Int,
        
        val sid: String
)
