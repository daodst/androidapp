

package org.matrix.android.sdk.internal.auth

import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.auth.data.SessionParams

internal interface SessionParamsStore {

    fun get(sessionId: String): SessionParams?

    fun getLast(): SessionParams?

    fun getAll(): List<SessionParams>

    suspend fun save(sessionParams: SessionParams)

    suspend fun setTokenInvalid(sessionId: String)

    suspend fun updateCredentials(newCredentials: Credentials)

    suspend fun delete(sessionId: String)

    suspend fun deleteAll()
}
