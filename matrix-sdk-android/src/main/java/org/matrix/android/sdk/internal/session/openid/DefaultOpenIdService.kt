

package org.matrix.android.sdk.internal.session.openid

import org.matrix.android.sdk.api.session.openid.OpenIdService
import org.matrix.android.sdk.api.session.openid.OpenIdToken
import javax.inject.Inject

internal class DefaultOpenIdService @Inject constructor(private val getOpenIdTokenTask: GetOpenIdTokenTask) : OpenIdService {

    override suspend fun getOpenIdToken(): OpenIdToken {
        return getOpenIdTokenTask.execute(Unit)
    }
}
