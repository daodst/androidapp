

package org.matrix.android.sdk.internal.auth.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.SsoIdentityProvider

@JsonClass(generateAdapter = true)
internal data class LoginFlowResponse(
        
        @Json(name = "flows")
        val flows: List<LoginFlow>?
)

@JsonClass(generateAdapter = true)
internal data class LoginFlow(
        
        @Json(name = "type")
        val type: String?,

        
        @Json(name = "identity_providers")
        val ssoIdentityProvider: List<SsoIdentityProvider>? = null

)
