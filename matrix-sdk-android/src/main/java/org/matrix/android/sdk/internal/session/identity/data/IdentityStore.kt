

package org.matrix.android.sdk.internal.session.identity.data

import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.internal.session.identity.model.IdentityHashDetailResponse

internal interface IdentityStore {

    fun getIdentityData(): IdentityData?

    fun setUrl(url: String?)

    fun setToken(token: String?)

    fun setUserConsent(consent: Boolean)

    fun setHashDetails(hashDetailResponse: IdentityHashDetailResponse)

    
    fun storePendingBinding(threePid: ThreePid, data: IdentityPendingBinding)

    fun getPendingBinding(threePid: ThreePid): IdentityPendingBinding?

    fun deletePendingBinding(threePid: ThreePid)
}

internal fun IdentityStore.getIdentityServerUrlWithoutProtocol(): String? {
    return getIdentityData()?.identityServerUrl?.substringAfter("://")
}
