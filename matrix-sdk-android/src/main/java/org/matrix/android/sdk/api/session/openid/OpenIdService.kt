

package org.matrix.android.sdk.api.session.openid

interface OpenIdService {

    
    suspend fun getOpenIdToken(): OpenIdToken
}
