

package org.matrix.android.sdk.api.auth

interface UIABaseAuth {
    
    val session: String?

    fun hasAuthInfo(): Boolean

    fun copyWithSession(session: String): UIABaseAuth

    fun asMap(): Map<String, *>
}
