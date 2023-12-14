

package org.matrix.android.sdk.internal.auth.db

import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.internal.auth.login.ResetPasswordData
import org.matrix.android.sdk.internal.auth.registration.ThreePidData
import java.util.UUID


internal data class PendingSessionData(
        val homeServerConnectionConfig: HomeServerConnectionConfig,

        

        val clientSecret: String = UUID.randomUUID().toString(),
        val sendAttempt: Int = 0,

        

        val resetPasswordData: ResetPasswordData? = null,

        

        val currentSession: String? = null,
        val isRegistrationStarted: Boolean = false,
        val currentThreePidData: ThreePidData? = null
)
