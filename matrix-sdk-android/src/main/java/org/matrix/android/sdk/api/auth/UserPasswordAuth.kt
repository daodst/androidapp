
package org.matrix.android.sdk.api.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes


@JsonClass(generateAdapter = true)
data class UserPasswordAuth(

        
        @Json(name = "session")
        override val session: String? = null,

        
        @Json(name = "type")
        val type: String? = LoginFlowTypes.PASSWORD,

        @Json(name = "user")
        val user: String? = null,

        @Json(name = "password")
        val password: String? = null
) : UIABaseAuth {

    override fun hasAuthInfo() = password != null

    override fun copyWithSession(session: String) = this.copy(session = session)

    override fun asMap(): Map<String, *> = mapOf(
            "session" to session,
            "user" to user,
            "password" to password,
            "type" to type
    )
}
