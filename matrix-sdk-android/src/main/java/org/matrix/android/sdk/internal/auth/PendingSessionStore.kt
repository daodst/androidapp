

package org.matrix.android.sdk.internal.auth

import org.matrix.android.sdk.internal.auth.db.PendingSessionData


internal interface PendingSessionStore {

    suspend fun savePendingSessionData(pendingSessionData: PendingSessionData)

    fun getPendingSessionData(): PendingSessionData?

    suspend fun delete()
}
