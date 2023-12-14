

package org.matrix.android.sdk.internal.federation

import org.matrix.android.sdk.api.federation.FederationService
import org.matrix.android.sdk.api.federation.FederationVersion
import javax.inject.Inject

internal class DefaultFederationService @Inject constructor(
        private val getFederationVersionTask: GetFederationVersionTask
) : FederationService {
    override suspend fun getFederationVersion(): FederationVersion {
        return getFederationVersionTask.execute(Unit)
    }
}
