
package org.matrix.android.sdk.api.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes


@JsonClass(generateAdapter = true)
data class TokenBasedAuth(

        
        @Json(name = "session")
        override val session: String? = null,

        
        @Json(name = "token")
        val token: String? = null,

        
        @Json(name = "txn_id")
        val transactionId: String? = null,

        
        @Json(name = "type")
        val type: String? = LoginFlowTypes.TOKEN

) : UIABaseAuth {
    override fun hasAuthInfo() = token != null

    override fun copyWithSession(session: String) = this.copy(session = session)

    override fun asMap(): Map<String, *> = mapOf(
            "session" to session,
            "token" to token,
            "transactionId" to transactionId,
            "type" to type
    )
}
