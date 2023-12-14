

package org.matrix.android.sdk.api.auth.registration

import org.matrix.android.sdk.api.session.Session

sealed class RegistrationResult {
    data class Success(val session: Session) : RegistrationResult()
    data class FlowResponse(val flowResult: FlowResult) : RegistrationResult()
}

data class FlowResult(
        val missingStages: List<Stage>,
        val completedStages: List<Stage>
)
