

package org.matrix.android.sdk.internal.session.identity

import org.matrix.android.sdk.internal.network.token.AccessTokenProvider
import org.matrix.android.sdk.internal.session.identity.data.IdentityStore
import javax.inject.Inject

internal class IdentityAccessTokenProvider @Inject constructor(
        private val identityStore: IdentityStore
) : AccessTokenProvider {
    override fun getToken() = identityStore.getIdentityData()?.token
}
