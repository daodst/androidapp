

package org.matrix.android.sdk.internal.network.token

import org.matrix.android.sdk.internal.auth.SessionParamsStore
import org.matrix.android.sdk.internal.di.SessionId
import javax.inject.Inject

internal class HomeserverAccessTokenProvider @Inject constructor(
        @SessionId private val sessionId: String,
        private val sessionParamsStore: SessionParamsStore
) : AccessTokenProvider {
    override fun getToken() = sessionParamsStore.get(sessionId)?.credentials?.accessToken
}
