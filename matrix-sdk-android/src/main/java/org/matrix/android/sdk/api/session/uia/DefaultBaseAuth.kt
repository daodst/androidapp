

package org.matrix.android.sdk.api.session.uia

import org.matrix.android.sdk.api.auth.UIABaseAuth

data class DefaultBaseAuth(
        
        override val session: String? = null

) : UIABaseAuth {
    override fun hasAuthInfo() = true

    override fun copyWithSession(session: String) = this.copy(session = session)

    override fun asMap(): Map<String, *> = mapOf("session" to session)
}
