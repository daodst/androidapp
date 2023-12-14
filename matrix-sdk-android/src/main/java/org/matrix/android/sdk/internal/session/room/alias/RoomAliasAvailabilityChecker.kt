

package org.matrix.android.sdk.internal.session.room.alias

import org.matrix.android.sdk.api.MatrixPatterns.getDomain
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.room.alias.RoomAliasError
import org.matrix.android.sdk.internal.di.UserId
import org.matrix.android.sdk.internal.network.GlobalErrorReceiver
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.session.directory.DirectoryAPI
import javax.inject.Inject

internal class RoomAliasAvailabilityChecker @Inject constructor(
        @UserId private val userId: String,
        private val directoryAPI: DirectoryAPI,
        private val globalErrorReceiver: GlobalErrorReceiver
) {
    
    @Throws(RoomAliasError::class)
    suspend fun check(aliasLocalPart: String?) {
        if (aliasLocalPart.isNullOrEmpty()) {
            
            return
        }
        if (aliasLocalPart.isBlank()) {
            throw RoomAliasError.AliasIsBlank
        }
        
        val fullAlias = aliasLocalPart.toFullLocalAlias(userId)
        try {
            executeRequest(globalErrorReceiver) {
                directoryAPI.getRoomIdByAlias(fullAlias)
            }
        } catch (throwable: Throwable) {
            if (throwable is Failure.ServerError && throwable.httpCode == 404) {
                
                return
            } else {
                
                throw throwable
            }
        }
                .let {
                    
                    throw RoomAliasError.AliasNotAvailable
                }
    }

    companion object {
        internal fun String.toFullLocalAlias(userId: String) = "#" + this + ":" + userId.getDomain()
    }
}
